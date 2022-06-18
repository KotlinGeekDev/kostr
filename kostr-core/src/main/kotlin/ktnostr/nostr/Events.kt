@file:JvmName("EventUtils")
package ktnostr.nostr

import fr.acinq.secp256k1.Hex
import ktnostr.crypto.CryptoUtils
import ktnostr.crypto.toHexString
import ktnostr.currentSystemTimestamp

object Events {

    fun generateEvent(eventKind: Int,
                      tags: List<Tag>,
                      content: String,
                      privateKey: String): Event {
        val pubkey = CryptoUtils.getPublicKey(Hex.decode(privateKey))
        val pubkeyString = pubkey.toHexString()
        val currentUnixTime = currentSystemTimestamp()
        val eventID = getEventId(pubkeyString, currentUnixTime, eventKind, tags, content)

        val eventIDRaw = Hex.decode(eventID)
        val signature = CryptoUtils.signContent(Hex.decode(privateKey), eventIDRaw)
        val signatureString = signature.toHexString()

        return Event(eventID, pubkeyString, currentUnixTime, eventKind, tags, content, signatureString)
    }

    @JvmStatic
    fun metadataEvent(privkey: String,
                      pubkey: String,
                      tags: List<Tag> = emptyList(),
                      kind: Int = EventKind.METADATA, content: String): Event {

        return generateEvent(kind, tags, content, privkey)
    }

    @JvmStatic
    fun textEvent(privkey: String, pubkey: String,
                  tags: List<Tag> = emptyList(),
                  kind: Int = EventKind.TEXT_NOTE, content: String): Event {
        return generateEvent(kind, tags, content, privkey)
    }

    @JvmStatic
    fun relayRecommendationEvent(privkey: String, pubkey: String,
                                 tags: List<Tag> = emptyList(),
                                 kind: Int = EventKind.RELAY_RECOMMENDATION,
                                 content: String): Event {
        if (!content.startsWith("wss") || !content.startsWith("ws")) {
            throw EventValidationError("Content $content is not a valid relay URL.")
            }
        return generateEvent(kind, tags, content, privkey)
    }

}