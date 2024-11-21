package ktnostr.net

import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.*
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
import kotlin.coroutines.CoroutineContext


class NostrService(private val relayPool: RelayPool, override val coroutineContext: CoroutineContext): CoroutineScope {
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
        onRelayMessage: suspend (relay: Relay, received: RelayMessage) -> Unit,
    ) {


        for (relay in relayPool.getRelays()) {
            requestFromRelay(
                requestMessage,
                relay,
                onRelayMessage = onRelayMessage,
                onRequestError = onRequestError
            )
        }
//        val requestJson = eventMapper.encodeToString(requestMessage)
//
//        for (relay in relayPool.getRelays()) {
//            println("Coroutine Scope @ ${relay.relayURI}")
//            try {
//                client.webSocket(urlString = relay.relayURI) {
//                    send(requestJson)
//
//                    for (frame in incoming) {
//                        val received = (frame as Frame.Text).readText()
//                        val receivedMessage = eventMapper.decodeFromString<RelayMessage>(received)
//                        onRelayMessage(relay, receivedMessage)
//                    }
//                }
//            } catch (e: kotlinx.io.IOException) {
//                onRequestError(relay, e)
//                if (client.isActive) this.client.cancel()
//            } catch (err: Exception) {
//                onRequestError(relay, err)
//            } catch (t: Throwable) {
//                onRequestError(relay, t)
//            }
//        }
    }

    private suspend fun requestFromRelay(
        requestMessage: RequestMessage,
        relay: Relay,
        onRelayMessage: suspend (Relay, RelayMessage) -> Unit,
        onRequestError: (Relay, Throwable) -> Unit
    ) {
        val requestJson = eventMapper.encodeToString(requestMessage)

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
        } catch (e: kotlinx.io.IOException) {
            onRequestError(relay, e)
            if (client.isActive) this.client.cancel()
        } catch (err: Exception) {
            onRequestError(relay, err)
        } catch (t: Throwable) {
            onRequestError(relay, t)
        }
    }

    suspend fun requestWithResult(
        requestMessage: RequestMessage,
        endpoints: List<Relay> = relayPool.getRelays()
    ): List<Event> {

        val results = mutableListOf<Event>()
        val relayAuthCache: MutableMap<Relay, RelayAuthMessage> = mutableMapOf()
        val jobs = mutableListOf<Job>()
        val requestJson = eventMapper.encodeToString(requestMessage)

        for (endpoint in endpoints) ret@{
            println("Coroutine Scope @ ${endpoint.relayURI}")
            val job = launch {
                try {
                    client.webSocket(endpoint.relayURI) {
                        send(requestJson)

                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val message = eventMapper.decodeFromString<RelayMessage>(frame.readText())
                                when (message) {
                                    is RelayEventMessage -> {
                                        println("Event message received from ${endpoint.relayURI}")
                                        val event = deserializedEvent(message.eventJson)
                                        println("Event created on ${formattedDateTime(event.creationDate)}")
                                        println(event.content)
                                        results.add(event)

                                    }

                                    is CountResponse -> {
                                        println("Received Count message: $message")
                                    }

                                    is RelayAuthMessage -> {
                                        println("Received Auth message: $message")
                                        serviceMutex.withLock {
                                            if (relayAuthCache.put(endpoint, message) != null){
                                                println("Added auth message <-$message-> to cache.")
                                            }
                                        }
                                    }

                                    is EventStatus -> {
                                        println("Received a status for the sent event:")
                                        println(message)
                                    }

                                    is RelayEose -> {
                                        if (relayEoseCount.value == endpoints.size){
                                            client.close()
//                                            if (isActive) break
//                                            break

                                        } else {
                                            relayEoseCount.update { it + 1 }
                                            println("Relay EOSE received from ${endpoint.relayURI}")
                                            println(message)
                                        }
                                        println("***********--")
                                        println("**List count: ${endpoints.size}")
                                        println("**EOSE count: ${relayEoseCount.value}")
                                        println("************--")
                                    }

                                    is CloseMessage -> {
                                        relayEoseCount.update { it + 1 }
                                        println("Closed by Relay ${endpoint.relayURI} with reason: ${message.errorMessage}")
                                    }

                                    is RelayNotice -> {
                                        println("Received a relay notice: $message")
                                    }
                                }
                                break
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("Failed to connect to $endpoint: ${e.message}")
                }
            }
            jobs.add(job)
        }

        jobs.forEach { it.join() }
        client.close()

        return results
    }

}

