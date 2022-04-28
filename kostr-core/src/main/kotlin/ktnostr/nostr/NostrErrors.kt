@file:JvmName("NostrErrors")
package ktnostr.nostr


sealed class NostrException : RuntimeException {
    constructor(): super()
    constructor(message: String?): super(message)
}

class EventValidationError(override val message: String): NostrException(message)
class UnsupportedKindError(override val message: String): NostrException(message)
class RelayError(override val message: String): NostrException(message)