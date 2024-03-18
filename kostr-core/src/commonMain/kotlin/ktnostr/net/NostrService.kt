package ktnostr.net

import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import ktnostr.formattedDateTime
import ktnostr.nostr.Event
import ktnostr.nostr.client.ClientEventMessage
import ktnostr.nostr.client.ClientMessage
import ktnostr.nostr.client.RequestMessage
import ktnostr.nostr.deserializedEvent
import ktnostr.nostr.eventMapper
import ktnostr.nostr.relays.*


class NostrService(private val relayPool: RelayPool) {
    private val relayNoticesCount = atomic(0)

    val client = httpClient {
        install(WebSockets){

        }

    }

    suspend fun publishEvent(event: ClientMessage) {
        when(event) {
            is ClientEventMessage -> sendEvent(event)
            is RequestMessage -> sendEvent(event)
            else -> println("Sending these types is not yet implemented.")
        }
    }





    suspend fun sendEvent(message: ClientMessage){
        val eventJson = eventMapper.encodeToString(message)
        relayPool.getRelays().forEach {
            client.webSocketSession(it.relayURI).send(eventJson)
        }
        client.close()
    }

    suspend fun request(requestMessage: RequestMessage,
                        onReceivedEvent: (Relay, Event) -> Unit,
                        onRelayNotice: (Relay, RelayNotice) -> Unit,
                     //   onError: (Exception) -> Unit
    ){
        val requestJson = eventMapper.encodeToString(requestMessage)
        coroutineScope {
            for (relay in relayPool.getRelays()) {
                launch {
                    client.webSocket(urlString = relay.relayURI) {
                        send(requestJson)

                        for (frame in incoming){
                            val received = (frame as Frame.Text).readText()
                            //println(received)

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
                }

            }
        }

    }

    suspend fun requestWithResult(requestMessage: RequestMessage): Result<List<Event>>? {
        val requestJson = eventMapper.encodeToString(requestMessage)
        val eventList = mutableListOf<Event>()
        var returnedResult : Result<List<Event>>? = null

        for ((relayIndex, relay) in relayPool.getRelays().withIndex()) {
            client.launch {
                client.webSocket(urlString = relay.relayURI) {
                    send(requestJson)

//                incoming.receive().let{ frame ->
//                    val received = (frame as Frame.Text).readText()
//                    println(received)
//
//
//                    val receivedMessage = eventMapper.readValue<RelayMessage>(received)
//
//                    when (receivedMessage) {
//                        is RelayEventMessage -> {
//                            println("Received from ${relay.relayURI}")
//                            val event = deserializedEvent(receivedMessage.eventJson)
//                            println("Event created on ${formattedDateTime(event.creationDate)}")
//                            println(event.content)
//                            returnedResult = Result.success(event)
//
//
//                        }
//
//                        is RelayNotice -> {
//                            println(receivedMessage)
//                        }
//
//                    }
//                }


                    incoming.consumeEach {
                        val json = (it as Frame.Text).readText()
                        println(json)

                        val receivedMessage = eventMapper.decodeFromString<RelayMessage>(json)

                        when (receivedMessage) {
                            is RelayEventMessage -> {
                                println("Received from ${relay.relayURI} with index $relayIndex")
                                val event = deserializedEvent(receivedMessage.eventJson)
                                println("Event created on ${formattedDateTime(event.creationDate)}")
                                println(event.content)
                                eventList.add(event)


                            }

                            is RelayNotice -> {
                                println("Received notice from ${relay.relayURI}. Index $relayIndex")
                                println(receivedMessage)
                                if (relayIndex == relayPool.getRelays().size - 1){
                                    client.cancel()
                                    if (eventList.isNotEmpty()) returnedResult = Result.success(eventList)
                                }
                            }

                        }

                    }
                    //close()
                }
            }

        }
        return returnedResult
    }

    suspend fun manualRequestWithResult(
        requestMessage: RequestMessage,
        relayList: List<Relay> = relayPool.getRelays()
    ): Result<Event> {
        val requestJson = eventMapper.encodeToString(requestMessage)
        var returnedResult: Result<Event>? = null
        val connections = relayList.mapIndexed { index: Int, relay: Relay ->

            client.launch {
                client.webSocket(urlString = relay.relayURI){
                    send(Frame.Text(requestJson))
                    incoming.consumeAsFlow().collect { frame ->
                        val received = (frame as Frame.Text).readText()
                        //println(received)


                        val receivedMessage = eventMapper.decodeFromString<RelayMessage>(received)
                        //println(receivedMessage.toString())

                        when (receivedMessage) {
                            is RelayEventMessage -> {
                                println("Event message received from ${relay.relayURI} with index $index")
                                val event = deserializedEvent(receivedMessage.eventJson)
                                println("Event created on ${formattedDateTime(event.creationDate)}")
                                println(event.content)
                                returnedResult = Result.success(event)

                            }

                            is RelayNotice -> {
                                relayNoticesCount.getAndIncrement()
                                println("Relay notice received from ${relay.relayURI} with index $index")
                                println(receivedMessage)
                                if (relayNoticesCount.value == relayList.size){
                                    client.cancel()
                                }

                            }

                        }
                    }
                }

            }

        }

        //connections.joinAll()
        //delay(1500L)
        connections.joinAll()


        return returnedResult!!
    }
}

