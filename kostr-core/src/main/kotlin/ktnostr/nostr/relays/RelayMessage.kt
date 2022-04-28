@file:JvmName("NostrRelayMessage")

package ktnostr.nostr.relays

sealed class RelayMessage

data class RelayEventMessage(val messageType: String, val subscriptionId: String,
                                val eventJson: String): RelayMessage()

data class RelayNotice(val messageType: String,
                       val message: String): RelayMessage()