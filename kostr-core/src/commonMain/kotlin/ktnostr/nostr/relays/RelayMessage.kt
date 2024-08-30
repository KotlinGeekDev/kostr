

package ktnostr.nostr.relays

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * The model that represents the data sent from a relay.
 * It is a base class for all specific relay message types.
 * Currently, there are two types of relay messages, represented by the tags EVENT and NOTICE.
 * You can see their corresponding representations, RelayEventMessage and RelayNotice.
 * @see RelayEventMessage
 * @see RelayNotice
 */
@Serializable(with = RelayMessage.RelayMessageSerializer::class)
sealed class RelayMessage(){
    internal companion object RelayMessageSerializer: KSerializer<RelayMessage> {
        private val listSerializer = ListSerializer(elementSerializer = String.serializer())
        override val descriptor: SerialDescriptor
            get() = listSerializer.descriptor

        override fun deserialize(decoder: Decoder): RelayMessage {
            val jsonMessageDecoder = decoder as JsonDecoder
            val messageJsonArray = jsonMessageDecoder.decodeJsonElement().jsonArray
            if (messageJsonArray.size > 4 || messageJsonArray.size < 2){
                println("Cannot decode JSON -> $messageJsonArray")
                throw SerializationException("Message type is not supported for message $messageJsonArray")
            }
            return if (messageJsonArray.size == 3){
                val messageMarker = messageJsonArray[0].jsonPrimitive.content
                val subscriptionId = messageJsonArray[1].jsonPrimitive.content
                val eventJsonNode = messageJsonArray[2]
                val eventJson = if (eventJsonNode.jsonObject.isEmpty()) "" else eventJsonNode.jsonObject.toString()
                RelayEventMessage(messageMarker, subscriptionId, eventJson)
            }
            else if (messageJsonArray.size == 4){
                val messageMarker = messageJsonArray[0].jsonPrimitive.content
                val eventId = messageJsonArray[1].jsonPrimitive.content
                val acceptedStatus = messageJsonArray[2].jsonPrimitive.boolean
                val additionalInfo = messageJsonArray[3].jsonPrimitive.content
                EventStatus(messageMarker, eventId, acceptedStatus, additionalInfo)
            }
            else {
                val noticeMarker = messageJsonArray[0].jsonPrimitive.content
                val noticeMessage = messageJsonArray[1].jsonPrimitive.content
                RelayNotice(noticeMarker, noticeMessage)
            }
        }

        override fun serialize(encoder: Encoder, value: RelayMessage) {
            val messageEncoder = (encoder as JsonEncoder).json
            val encodedMessage = buildJsonArray {
                when(value){
                    is RelayEventMessage -> {
                        val messageTypeMarker = messageEncoder.encodeToJsonElement(value.messageType)
                        val subscriptionMarker = messageEncoder.encodeToJsonElement(value.subscriptionId)
                        val eventJsonMarker = messageEncoder.encodeToJsonElement(value.eventJson)
                        add(messageTypeMarker)
                        add(subscriptionMarker)
                        add(eventJsonMarker)
                    }
                    is EventStatus -> {
                        val messageTypeMarker = messageEncoder.encodeToJsonElement(value.messageType)
                        val eventId = messageEncoder.encodeToJsonElement(value.eventId)
                        val acceptStatus = messageEncoder.encodeToJsonElement(value.accepted)
                        val additionalInfo = messageEncoder.encodeToJsonElement(value.additionalInfo)
                        add(messageTypeMarker)
                        add(eventId)
                        add(acceptStatus)
                        add(additionalInfo)
                    }
                    is RelayNotice -> {
                        val messageTypeMarker = messageEncoder.encodeToJsonElement(value.messageType)
                        val contentMarker = messageEncoder.encodeToJsonElement(value.message)
                        add(messageTypeMarker)
                        add(contentMarker)
                    }
                }
            }
            encoder.encodeJsonElement(encodedMessage)
        }

    }
}

/**
 * The model representing the case when a relay sends the data we request.
 * Typically, the data is a JSON array of 3 elements, which look like this: [[EVENT, subscriptionId, eventJson]].
 * Though the eventJson returned here is in a String format, it will need to be parsed for a client to make
 * sense of it. You can do so using the provided deserializedEvent() function.
 * @see ktnostr.nostr.deserializedEvent
 */

@Serializable
data class RelayEventMessage(
    val messageType: String = "EVENT",
    val subscriptionId: String,
    val eventJson: String
) : RelayMessage()

/**
 * Represents the relay response to an event being sent to it.
 * It is of the form [[OK, event-id, <true|false>, message]].
 */
data class EventStatus(
    val messageType: String = "OK",
    val eventId: String,
    val accepted: Boolean,
    val additionalInfo: String
): RelayMessage()

/**
 * The model representing the case when the relay returns a message different from the normal response.
 * The data is a JSON array of 2 elements, which is of the form: [[NOTICE, message]].
 * This could be due to the relay not having the data we need, or something else.
 */

@Serializable
data class RelayNotice(
    val messageType: String,
    val message: String
) : RelayMessage()




//private class RelayMessageConverter : JsonDeserializer<RelayMessage>() {
//
//    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): RelayMessage {
//        val messageTree = p?.readValueAsTree<ArrayNode>()
//        if (messageTree?.size()!! > 3 || messageTree.size() < 2) {
//            throw Exception("This message type is not supported.")
//        }
//
//
//        return if (messageTree.size() == 3) {
//            val messageType = messageTree[0].asText()
//            val subscriptionId = messageTree[1].asText()
//            val content = if (messageTree[2].isContainerNode) messageTree[2].toString() else ""
//            RelayEventMessage(messageType, subscriptionId, content)
//        } else {
//            val messageType = messageTree[0].asText()
//            val message = messageTree[1].asText()
//            RelayNotice(messageType, message)
//        }
//    }
//
//}


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