package ktnostr.nostr.relays

import ktnostr.nostr.RelayError

class RelayPool {

    private lateinit var relayList: MutableList<Relay>
    constructor(){
        relayList = getDefaultRelays().toMutableList()
    }

    constructor(relays: List<Relay>) : this() {
        this.relayList = relays as MutableList<Relay>
    }

    fun getRelays() = if (relayList.isEmpty()) getDefaultRelays() else relayList.toList()

    fun addRelay(relay: Relay) {
        if (relayList.add(relay))
            return
        else throw RelayError("The relay ${relay.relayURI} could not be added.")
    }

    fun removeRelay(relay: Relay) {
        relayList.remove(relay)
    }

    companion object {

        @JvmStatic
        fun getDefaultRelays(): List<Relay> = listOf(
            Relay("wss://nostr-pub.wellorder.net"),
            Relay("wss://relay.damus.io"),
            Relay("wss://relay.nostr.wirednet.jp"),
            Relay("wss://relay.nostr.band"),
            Relay("wss://nostr.inosta.cc")
        )
    }

}


