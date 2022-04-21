@file:JvmName("EventExt")
package ktnostr.nostr

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.acinq.secp256k1.Hex
import ktnostr.crypto.CryptoUtils

val eventMapper = jacksonObjectMapper()

/**
 * Keeping this function below for legacy purposes.
 */
//fun Event.toJson(): String {
//
//    val listOfTagsJson = if (tags.isEmpty()) "[]"
//    else buildString {
//        append("[")
//        tags.forEach { tagJson ->
//            if (tagJson.size == 3) {
//                val tag = Tag(tagJson[0], tagJson[1], tagJson[2])
//                append(
//                    "[\"${tag.identifier}\",\"${tag.description}\"${
//                        if (tag.recommendedRelayUrl.isNullOrEmpty()) ""
//                        else ",\"${tag.recommendedRelayUrl}\""
//                    }]"
//                )
//                append(",")
//            }
//
//        }
//        deleteAt(lastIndexOf(","))
//        append("]")
//    }
//
//    val event = buildString {
//        append("{")
//        append("\"id\":\"$id\",")
//        append("\"pubkey\":\"$pubkey\",")
//        append("\"created_at\":$creationDate,")
//        append("\"kind\":$eventKind,")
//        append("\"tags\":$listOfTagsJson,")
//        append("\"content\":\"$content\",")
//        append("\"sig\":\"$eventSignature\"")
//        append("}")
//    }
//
//    return event
//}

fun Event.isValid(): Boolean {
    val eventId = getEventId(this.pubkey, this.creationDate, this.eventKind,
                this.tags, this.content)
    if (eventId != this.id){
        println("The event id is invalid.")
        return false
    }
    val signatureValidity = CryptoUtils.get().verifyContentSignature(
        Hex.decode(this.eventSignature),
        Hex.decode(this.pubkey), Hex.decode(eventId))
    if (!signatureValidity){
        println("The event signature is invalid.\n Please check the pubkey, or content.")
        return false
    }
    return true
}

fun Event.serialize(): String {
    if (!this.isValid()) throw EventValidationError("Generated event is not valid")
    val serializedEvent = eventMapper.writeValueAsString(this)
    return serializedEvent
}

fun deserializedEvent(eventJson: String): Event {
    val deserializedEvent = eventMapper.readValue<Event>(eventJson)
    if (!deserializedEvent.isValid()) throw EventValidationError("The event is invalid.")
    return deserializedEvent
}
