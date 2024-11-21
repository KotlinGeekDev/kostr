package ktnostr.net

import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.utils.io.errors.*
import io.ktor.websocket.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import ktnostr.formattedDateTime
import ktnostr.nostr.Event
import ktnostr.nostr.client.ClientMessage
import ktnostr.nostr.client.RequestMessage
import ktnostr.nostr.deserializedEvent
import ktnostr.nostr.eventMapper
import ktnostr.nostr.relay.*


class NostrService(private val relayPool: RelayPool) {
    private val relayEoseCount = atomic(0)
    private val serviceMutex = Mutex()

    private val client = httpClient {
        install(WebSockets){

        }

        install(Logging){

        }
    }

//    suspend fun publishEvent(event: ClientMessage) {
//        when(event) {
//            is ClientEventMessage -> sendEvent(event)
//            is RequestMessage -> sendEvent(event)
//            else -> println("Sending these types is not yet implemented.")
//        }
//    }

    suspend fun sendEvent(message: ClientMessage, onRelayMessage: (Relay, RelayMessage) -> Unit){
        val eventJson = eventMapper.encodeToString(message)
        relayPool.getRelays().forEach {
            client.webSocket(it.relayURI){
                send(eventJson)
                for (frame in incoming){
                    val messageJson = (frame as Frame.Text).readText()
                    val decodedMessage = eventMapper.decodeFromString<RelayMessage>(messageJson)
                    onRelayMessage(it, decodedMessage)

                }
            }
        }
        client.close()
    }

    suspend fun request(
        requestMessage: RequestMessage,
        onRequestError: (Relay, Throwable) -> Unit,
        onRelayMessage: (relay: Relay, received: RelayMessage) -> Unit,
    ) {
        val requestJson = eventMapper.encodeToString(requestMessage)
        coroutineScope {
            for (relay in relayPool.getRelays()) {
                launch relayScope@{
                    println("Coroutine Scope @ ${relay.relayURI}")
                    try {
                        client.webSocket(urlString = relay.relayURI) {
                            send(requestJson)

                            for (frame in incoming) {
                                val received = (frame as Frame.Text).readText()
                                val receivedMessage = eventMapper.decodeFromString<RelayMessage>(received)
                                onRelayMessage(relay, receivedMessage)
                            }
                        }
                    } catch (e: IOException) {
                        onRequestError(relay, e)
                        if (isActive) this@relayScope.cancel()
                    } catch (err: Exception) {
                        onRequestError(relay, err)
                    } catch (t: Throwable) {
                        onRequestError(relay, t)
                    }
                }
            }
        }
    }


    suspend fun requestWithResult(
        requestMessage: RequestMessage,
        relayList: List<Relay> = relayPool.getRelays()
    ): Result<List<Event>> {
        val requestJson = eventMapper.encodeToString(requestMessage)
        val relayAuthCache: MutableMap<Relay, RelayAuthMessage> = mutableMapOf()
        val eventResultList = emptyList<Event>().toMutableList()
        var returnedResult: Result<List<Event>> = Result.failure(Exception("Empty list"))
        val connectionJobs = relayList.mapIndexed { index: Int, relay: Relay ->
            client.launch {
                try {
                    client.webSocket(urlString = relay.relayURI){
                        send(Frame.Text(requestJson))
                        incoming.consumeAsFlow().collect { frame ->
                            ensureActive()
                            val received = (frame as Frame.Text).readText()
                            val receivedMessage = eventMapper.decodeFromString<RelayMessage>(received)

                            when (receivedMessage) {
                                is RelayEventMessage -> {
                                    println("Event message received from ${relay.relayURI} with index $index")
                                    val event = deserializedEvent(receivedMessage.eventJson)
                                    println("Event created on ${formattedDateTime(event.creationDate)}")
                                    println(event.content)
                                    eventResultList.add(event)
                                }

                                is CountResponse -> {
                                    println("Received Count message: $receivedMessage")
                                }

                                is RelayAuthMessage -> {
                                    println("Received Auth message: $receivedMessage")
                                    serviceMutex.withLock {
                                        if (relayAuthCache.put(relay, receivedMessage) != null){
                                            println("Added auth message <-$receivedMessage-> to cache.")
                                        }
                                    }
                                }

                                is EventStatus -> {
                                    println("Received a status for the sent event:")
                                    println(receivedMessage)
                                }

                                is RelayEose -> {
                                    if (relayEoseCount.value == relayList.size){
                                        close()
                                        if (eventResultList.isNotEmpty())
                                            returnedResult = Result.success(eventResultList.toList())

                                    } else {
                                        relayEoseCount.update { it + 1 }
                                        println("Relay EOSE received from ${relay.relayURI} with index $index")
                                        println(receivedMessage)
                                    }
                                    println("***********--")
                                    println("**List count: ${relayList.size}")
                                    println("**EOSE count: ${relayEoseCount.value}")
                                    println("************--")
                                }

                                is CloseMessage -> {
                                    relayEoseCount.update { it + 1 }
                                    println("Closed by Relay ${relay.relayURI} with reason: ${receivedMessage.errorMessage}")
                                }

                                is RelayNotice -> {
                                    println("Received a relay notice: $receivedMessage")
                                }
                            }
                        }
                    }
                } catch (e: IOException) {
                    if (relayEoseCount.value == relayList.size){
                        println("Terminating...")
                        if (eventResultList.isNotEmpty())
                            returnedResult = Result.success(eventResultList.toList())

                        client.cancel()
                    } else {
                        relayPool.removeRelay(relay)
                        relayEoseCount.update { it + 1 }
                        println("***********--")
                        println("**List count: ${relayList.size}")
                        println("**EOSE count: ${relayEoseCount.value}")
                        println("************--")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        connectionJobs.joinAll()

        return returnedResult
    }

}

