@file:JvmName("EventUtils")
package ktnostr.nostr

import ktnostr.crypto.CryptoUtils
import ktnostr.crypto.toBytes
import ktnostr.crypto.toHexString
import ktnostr.currentSystemTimestamp

object Events {

    fun generateEvent(eventKind: Int,
                      tags: List<Tag>,
                      content: String,
                      privateKeyHex: String, publicKeyHex: String): Event {
        val currentUnixTime = currentSystemTimestamp()
        val eventID = getEventId(publicKeyHex, currentUnixTime, eventKind, tags, content)

        val eventIDRaw = eventID.toBytes()
        val signature = CryptoUtils.signContent(privateKeyHex.toBytes(), eventIDRaw)
        val signatureString = signature.toHexString()

        return Event(eventID, publicKeyHex, currentUnixTime, eventKind, tags, content, signatureString)
    }

    @JvmStatic
    fun metadataEvent(privkey: String,
                      pubkey: String,
                      tags: List<Tag> = emptyList(),
                      kind: Int = EventKind.METADATA, content: String): Event {

        return generateEvent(kind, tags, content, privkey, pubkey)
    }

    @JvmStatic
    fun textEvent(privkey: String, pubkey: String,
                  tags: List<Tag> = emptyList(),
                  kind: Int = EventKind.TEXT_NOTE, content: String): Event {
        return generateEvent(kind, tags, content, privkey, pubkey)
    }

    @JvmStatic
    fun relayRecommendationEvent(privkey: String, pubkey: String,
                                 tags: List<Tag> = emptyList(),
                                 kind: Int = EventKind.RELAY_RECOMMENDATION,
                                 content: String): Event {
        if (!content.startsWith("wss") || !content.startsWith("ws")) {
            throw EventValidationError("Content $content is not a valid relay URL.")
            }
        return generateEvent(kind, tags, content, privkey, pubkey)
    }

}