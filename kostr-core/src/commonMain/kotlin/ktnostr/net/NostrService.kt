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
import ktnostr.nostr.relays.*


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

    suspend fun sendEvent(message: ClientMessage){
        val eventJson = eventMapper.encodeToString(message)
        relayPool.getRelays().forEach {
            client.webSocket(it.relayURI){
                send(eventJson)
                for (frame in incoming){
                    val messageJson = (frame as Frame.Text).readText()
                    val decodedMessage = eventMapper.decodeFromString<RelayMessage>(messageJson)

                }
            }
        }
        client.close()
    }

    suspend fun request(
        requestMessage: RequestMessage,
        onReceivedEvent: (Relay, Event) -> Unit,
        onAuthRequest: (Relay, RelayAuthMessage) -> Unit,
        onEose: (Relay, RelayEose) -> Unit,
        onRelayNotice: (Relay, RelayNotice) -> Unit,
        onError: (Relay, Throwable) -> Unit
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

                                when(receivedMessage){
                                    is RelayEventMessage -> {
                                        val event = deserializedEvent(receivedMessage.eventJson)
                                        onReceivedEvent(relay, event)
                                    }

                                    is RelayAuthMessage -> {
                                        println("Received Auth message: $receivedMessage")
                                        onAuthRequest(relay, receivedMessage)
                                    }

                                    is EventStatus -> {
                                        println("Received a status for the sent event: \n $receivedMessage")
                                    }

                                    is RelayEose -> {
                                        onEose(relay, receivedMessage)
                                    }

                                    is CloseMessage -> {
                                        onError(relay, Exception(receivedMessage.errorMessage))
                                    }

                                    is RelayNotice -> {
                                        onRelayNotice(relay, receivedMessage)
                                    }
                                }
                            }
                        }
                    } catch (e: IOException) {
                        onError(relay, e)
                        println("Terminating connection to ${relay.relayURI}...")
                        if (isActive) this@relayScope.cancel()
                    } catch (err: Exception) {
                        onError(relay, err)
                    } catch (t: Throwable) {
                        onError(relay, t)
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
        val connections = relayList.mapIndexed { index: Int, relay: Relay ->
            client.launch {
                try {
                    client.webSocket(urlString = relay.relayURI){
                        send(Frame.Text(requestJson))
                        incoming.consumeAsFlow().collect { frame ->
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
                                    if (relayEoseCount.value == relayPool.getRelays().size){
                                        close()
                                        if (eventResultList.isNotEmpty())
                                            returnedResult = Result.success(eventResultList.toList())

                                    } else {
                                        relayEoseCount.update { it + 1 }
                                        println("Relay EOSE received from ${relay.relayURI} with index $index")
                                        println(receivedMessage)
                                    }
                                    println("***********--")
                                    println("**List count: ${relayPool.getRelays().size}")
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
                    println("Network error for Relay:${relay.relayURI}-> ${e.message}")
                    if (e.message?.contains("Failed to connect") == true) {
                        relayPool.removeRelay(relay)
                    }
                    relayEoseCount.update { it + 1 }
                    println("***********--")
                    println("**List count: ${relayPool.getRelays().size}")
                    println("**EOSE count: ${relayEoseCount.value}")
                    println("************--")
                    if (relayEoseCount.value == relayPool.getRelays().size){
                        println("Terminating...")
                        client.cancel()
                        if (eventResultList.isNotEmpty())
                            returnedResult = Result.success(eventResultList.toList())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        connections.joinAll()

        return returnedResult
    }

}

