package ktnostr.nostr.client

import com.fasterxml.jackson.annotation.JsonFormat
import ktnostr.nostr.Event
import ktnostr.nostr.NostrFilter

sealed class ClientMessage(open val messageType: String) {
}

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class ClientEventMessage(override val messageType: String = "EVENT",
                              val event: Event): ClientMessage(messageType)

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class RequestMessage(override val messageType: String = "REQ",
                          val subscriptionId: String, 
                          val filter: NostrFilter): ClientMessage(messageType)

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class CloseRequest(override val messageType: String = "CLOSE",
                        val subscriptionId: String): ClientMessage(messageType)