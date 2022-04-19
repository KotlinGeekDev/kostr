@file:JvmName("Events")
package ktnostr.nostr

import com.fasterxml.jackson.annotation.JsonProperty
import fr.acinq.secp256k1.Hex
import ktnostr.crypto.CryptoUtils
import ktnostr.crypto.toHexString
import ktnostr.currentSystemUnixTimeStamp

/**
 * The Event class representing the Nostr Event.
 * The Event is of the form:
 *     Event(event_id, pubkey, creation_date, kind, tags, content, signature)
 * @param id The event id, as a string
 * @param pubkey The public key of the event's author, as a 32-byte string
 * @param creationDate The Unix timestamp of the event, as a number
 * @param eventKind The event kind, as a number
 * @param tags The list of tags associated with the event
 * @param content The event's content, as a string
 * @param eventSignature The event's signature, as a 64-byte string
 */
data class Event( val id: String,
             val pubkey: String,
             @JsonProperty("created_at") val creationDate: Long,
             @JsonProperty("kind") val eventKind: Int,
             val tags: List<List<String>>,
             val content: String,
             @JsonProperty("sig") val eventSignature: String): java.io.Serializable

fun generateEvent(eventKind: Int, tags: List<List<String>>, content: String, privateKey: String): Event {
    val pubkey = CryptoUtils.get().getPublicKey(Hex.decode(privateKey))
    val pubkeyString = pubkey.toHexString()
    val currentUnixTime = currentSystemUnixTimeStamp()
    val eventID = getEventId(pubkeyString, currentUnixTime, eventKind, tags, content)

    val eventIDRaw = Hex.decode(eventID)
    val signature = CryptoUtils.get().signContent(Hex.decode(privateKey), eventIDRaw)
    val signatureString = signature.toHexString()

    return Event(eventID, pubkeyString, currentUnixTime, eventKind, tags, content, signatureString)
}


fun getEventId(
    pubkey: String, timeStamp: Long, eventKind: Int,
    tags: List<List<String>>, content: String
): String {
    val jsonToHash = rawEventJson0(pubkey, timeStamp, eventKind, tags, content)

    val jsonHash = CryptoUtils.get().contentHash(jsonToHash)
    return jsonHash.toHexString()
}

internal fun rawEventJson0(pubkey: String, timeStamp: Long, eventKind: Int,
                           tags: List<List<String>>, content: String): String {
    val rawEventForSerialization = listOf(0, pubkey, timeStamp, eventKind, tags, content)

    val serializedRawEvent = eventMapper.writeValueAsString(rawEventForSerialization)
    return serializedRawEvent
}


/**
 * This function is kept here for legacy purposes
 */
//internal fun rawEventJson(pubkey: String, timeStamp: Long, eventKind: Int,
//                 tags: List<List<String>>, content: String): String {
//
//    val listOfTagsJson = if (tags.isEmpty()) "[]"
//    else buildString {
//        append("[")
//        tags.forEach { tagArray ->
//            if(tagArray.size < 2) throw Error("Invalid tag structure")
//            if (tagArray.size ==3) {
//                val tag = Tag(tagArray[0], tagArray[1], tagArray[2])
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
//    val jsonEventRaw = buildString {
//        append("[")
//        append("0,")
//        append("\"$pubkey\",")
//        append("$timeStamp,")
//        append("$eventKind,")
//        append("$listOfTagsJson,")
//        append("\"$content\"")
//        append("]")
//    }
//    return jsonEventRaw
//}


/**
 * This object represents the various event kinds
 * currently used on Nostr. Not all are supported, though.
 */
object EventKind {
    const val METADATA = 0
    const val TEXT_NOTE = 1
    const val RELAY_RECOMMENDATION = 2
    const val CONTACT_LIST = 3
    const val ENCRYPTED_DM = 4
    const val MARK_FOR_DELETION = 5

}


