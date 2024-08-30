package ktnostr.nostr.relays

class Relay(
    val relayURI: String,
    val readPolicy: Boolean = true,
    val writePolicy: Boolean = true
) {
    companion object {
        fun fromUrl(address: String): Relay {
            return Relay(address)
        }
    }
}