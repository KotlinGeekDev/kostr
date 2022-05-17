@file:JvmName("NostrRelayMessage")

package ktnostr.nostr.relays

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.module.kotlin.readValue
import ktnostr.nostr.RelayMessageError
import ktnostr.nostr.eventMapper

@JsonSubTypes(
    JsonSubTypes.Type(RelayEventMessage::class),
    JsonSubTypes.Type(RelayNotice::class)
)
sealed class RelayMessage

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class RelayEventMessage(val messageType: String, val subscriptionId: String,
                                val eventJson: String): RelayMessage()

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class RelayNotice(val messageType: String,
                       val message: String): RelayMessage()

//This function is kept for legacy and last-resort purposes.
fun String.toRelayMessage(): RelayMessage {
    val json = this
    val eventMessageRaw = eventMapper.readValue<List<String>>(json)
    if (eventMessageRaw.size > 3 || eventMessageRaw.size < 2){
        println("Unsupported relay message")
        throw RelayMessageError("This relay message cannot be understood.\n Message: $eventMessageRaw")
    }
    else {
        return when(eventMessageRaw.first()){
            "EVENT" -> RelayEventMessage(eventMessageRaw[0], eventMessageRaw[1], eventMessageRaw[2])
            "NOTICE" -> RelayNotice(eventMessageRaw[0], eventMessageRaw[1])
            else -> throw RelayMessageError("This message does not conform to the standard format of" +
                    "relay messages. \n MessageJson: $this")
        }
    }
}