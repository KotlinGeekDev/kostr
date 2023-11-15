package ktnostr.nostr.relays

class Relay(
    val relayURI: String,
    val readPolicy: Boolean = true,
    val writePolicy: Boolean = true
)