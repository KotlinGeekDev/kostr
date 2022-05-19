@file:JvmName("NostrRelayMessage")

package ktnostr.nostr.relays

//import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ArrayNode

@JsonDeserialize(using = RelayMessageConverter::class)
sealed class RelayMessage

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class RelayEventMessage(val messageType: String, val subscriptionId: String,
                             val eventJson: String): RelayMessage()

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class RelayNotice(val messageType: String,
                       val message: String): RelayMessage()

class RelayMessageConverter: JsonDeserializer<RelayMessage>() {

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): RelayMessage {
        val messageTree = p?.readValueAsTree<ArrayNode>()
        if (messageTree?.size()!! > 3 || messageTree.size() < 2) {throw Exception("This message type is not supported.")}


        if (messageTree.size() == 3){
            val messageType = messageTree[0].asText()
            val subscriptionId = messageTree[1].asText()
            val content = if (messageTree[2].isContainerNode) messageTree[2].toString() else ""
            return RelayEventMessage(messageType, subscriptionId, content)
        } else {
            val messageType = messageTree[0].asText()
            val message = messageTree[1].asText()
            return RelayNotice(messageType, message)
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