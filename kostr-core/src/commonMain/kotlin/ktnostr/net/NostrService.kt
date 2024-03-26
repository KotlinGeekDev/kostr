package ktnostr.net

import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.utils.io.errors.*
import io.ktor.websocket.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.serialization.encodeToString
import ktnostr.formattedDateTime
import ktnostr.nostr.Event
import ktnostr.nostr.client.ClientMessage
import ktnostr.nostr.client.RequestMessage
import ktnostr.nostr.deserializedEvent
import ktnostr.nostr.eventMapper
import ktnostr.nostr.relays.*


class NostrService(private val relayPool: RelayPool) {
    private val relayNoticesCount = atomic(0)

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
            client.webSocketSession(it.relayURI).send(eventJson)
        }
        client.close()
    }

    suspend fun request(
        requestMessage: RequestMessage,
        onReceivedEvent: (Relay, Event) -> Unit,
        onRelayNotice: (Relay, RelayNotice) -> Unit,
        onError: (Throwable) -> Unit
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

                                    is RelayNotice -> {
                                        onRelayNotice(relay, receivedMessage)

                                    }
                                }
                            }
                        }
                    } catch (e: IOException) {
                        onError(e)
                        println("Terminating connection to ${relay.relayURI}...")
                        if (isActive) this@relayScope.cancel()
                    } catch (err: Exception) {
                        onError(err)
                    } catch (t: Throwable) {
                        onError(t)
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

                                is RelayNotice -> {
                                    if (relayNoticesCount.value == relayPool.getRelays().size){
                                        close()
                                        if (eventResultList.isNotEmpty())
                                            returnedResult = Result.success(eventResultList.toList())

                                    } else {
                                        relayNoticesCount.update { it + 1 }
                                        println("Relay notice received from ${relay.relayURI} with index $index")
                                        println(receivedMessage)
                                    }
                                    println("***********--")
                                    println("**List count: ${relayPool.getRelays().size}")
                                    println("**Notice count: ${relayNoticesCount.value}")
                                    println("************--")
                                }
                            }
                        }
                    }
                } catch (e: IOException) {
                    println("Network error for Relay:${relay.relayURI}-> ${e.message}")
                    if (e.message?.contains("Failed to connect") == true) {
                        relayPool.removeRelay(relay)
                    }
                    relayNoticesCount.update { it + 1 }
                    println("***********--")
                    println("**List count: ${relayPool.getRelays().size}")
                    println("**Notice count: ${relayNoticesCount.value}")
                    println("************--")
                    if (relayNoticesCount.value == relayPool.getRelays().size){
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

