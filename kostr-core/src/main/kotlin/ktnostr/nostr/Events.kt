@file:JvmName("EventUtils")
package ktnostr.nostr

import fr.acinq.secp256k1.Secp256k1
import ktnostr.crypto.CryptoUtils
import ktnostr.crypto.toBytes
import ktnostr.crypto.toHexString
import ktnostr.currentSystemTimestamp

object Events {

    internal fun generateEvent(eventKind: Int,
                               tags: List<Tag>,
                               content: String,
                               privateKeyHex: String,
                               publicKeyHex: String, timeStamp: Long = currentSystemTimestamp()): Event {
        //val currentUnixTime = currentSystemTimestamp()
        val privKey = privateKeyHex.toBytes()
        val genPubKey = Secp256k1.pubkeyCreate(privKey).drop(1).take(32).toByteArray()
        val genPubKeyHex = genPubKey.toHexString()
        if (genPubKeyHex != publicKeyHex)
            throw EventValidationError("The pubkeys don't match. Expected :$publicKeyHex \n Generated:$genPubKeyHex ")
        val eventID = getEventId(publicKeyHex, timeStamp, eventKind, tags, content)

        val eventIDRaw = eventID.toBytes()
        val signature = CryptoUtils.signContent(privateKeyHex.toBytes(), eventIDRaw)
        val signatureString = signature.toHexString()

        return Event(eventID, publicKeyHex, timeStamp, eventKind, tags, content, signatureString)
    }

    @JvmStatic
    fun MetadataEvent(privkey: String,
                      pubkey: String,
                      timeStamp: Long = currentSystemTimestamp(),
                      tags: List<Tag> = emptyList(),
                      kind: Int = EventKind.METADATA, profile: String): Event {

        return generateEvent(kind, tags, profile, privkey, pubkey, timeStamp)
    }

    @JvmStatic
    fun TextEvent(privkey: String, pubkey: String,
                  tags: List<Tag> = emptyList(),
                  kind: Int = EventKind.TEXT_NOTE, content: String): Event {
        return generateEvent(kind, tags, content, privkey, pubkey)
    }

    @JvmStatic
    fun RelayRecommendationEvent(privkey: String, pubkey: String,
                                 tags: List<Tag> = emptyList(),
                                 kind: Int = EventKind.RELAY_RECOMMENDATION,
                                 content: String): Event {
        if (!(content.startsWith("wss") || content.startsWith("ws"))) {
            throw EventValidationError("Content $content is not a valid relay URL.")
            }
        return generateEvent(kind, tags, content, privkey, pubkey)
    }

    @JvmStatic
    fun FollowEvent(privkey: String, pubkey: String,
                    tags: List<Tag>,
                    kind: Int = EventKind.CONTACT_LIST,
                    content: String): Event {
        return generateEvent(kind, tags, content, privkey, pubkey)
    }

    @JvmStatic
    fun DirectMessageEvent(privkey: String, pubkey: String,
                           tags: List<Tag> = emptyList(),
                           kind: Int = EventKind.ENCRYPTED_DM,
                           content: String): Event {
        return generateEvent(kind, tags, content, privkey, pubkey)
    }

    @JvmStatic
    fun DeletionEvent(privkey: String, pubkey: String,
                      tags: List<Tag> = emptyList(),
                      kind: Int = EventKind.MARKED_FOR_DELETION,
                      content: String): Event {
        return generateEvent(kind, tags, content, privkey, pubkey)
    }

}