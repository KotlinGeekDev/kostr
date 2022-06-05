@file:JvmName("NostrRelayMessage")

package ktnostr.nostr.relays

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ArrayNode

/**
 * The model that represents the data sent from a relay.
 * It is a base class for all specific relay message types.
 * Currently, there are two types of relay messages, represented by the tags EVENT and NOTICE.
 * You can see their corresponding representations, RelayEventMessage and RelayNotice.
 * @see RelayEventMessage
 * @see RelayNotice
 */
@JsonDeserialize(using = RelayMessageConverter::class)
sealed class RelayMessage

/**
 * The model representing the case when a relay sends the data we request.
 * Typically, the data is a JSON array of 3 elements, which look like this: [[EVENT, subscriptionId, eventJson]].
 * Though the eventJson returned here is in a String format, it will need to be parsed for a client to make
 * sense of it. You can do so using the provided deserializedEvent() function.
 * @see ktnostr.nostr.deserializedEvent
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class RelayEventMessage(
    val messageType: String = "EVENT", val subscriptionId: String,
    val eventJson: String
) : RelayMessage()

/**
 * The model representing the case when the relay returns a message different from the normal response.
 * The data is a JSON array of 2 elements, which is of the form: [[NOTICE, message]].
 * This could be due to the relay not having the data we need, or something else.
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class RelayNotice(
    val messageType: String,
    val message: String
) : RelayMessage()

private class RelayMessageConverter : JsonDeserializer<RelayMessage>() {

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): RelayMessage {
        val messageTree = p?.readValueAsTree<ArrayNode>()
        if (messageTree?.size()!! > 3 || messageTree.size() < 2) {
            throw Exception("This message type is not supported.")
        }


        return if (messageTree.size() == 3) {
            val messageType = messageTree[0].asText()
            val subscriptionId = messageTree[1].asText()
            val content = if (messageTree[2].isContainerNode) messageTree[2].toString() else ""
            RelayEventMessage(messageType, subscriptionId, content)
        } else {
            val messageType = messageTree[0].asText()
            val message = messageTree[1].asText()
            RelayNotice(messageType, message)
        }
    }

}


//----This function is kept for legacy and last-resort purposes----
//fun String.toRelayMessage(): RelayMessage {
//    val json = this
//    val eventMessageRaw = eventMapper.readValue<List<String>>(json)
//    if (eventMessageRaw.size > 3 || eventMessageRaw.size < 2){
//        println("Unsupported relay message")
//        throw RelayMessageError("This relay message cannot be understood.\n Message: $eventMessageRaw")
//    }
//    else {
//        return when(eventMessageRaw.first()){
//            "EVENT" -> RelayEventMessage(eventMessageRaw[0], eventMessageRaw[1], eventMessageRaw[2])
//            "NOTICE" -> RelayNotice(eventMessageRaw[0], eventMessageRaw[1])
//            else -> throw RelayMessageError("This message does not conform to the standard format of" +
//                    "relay messages. \n MessageJson: $this")
//        }
//    }
//}