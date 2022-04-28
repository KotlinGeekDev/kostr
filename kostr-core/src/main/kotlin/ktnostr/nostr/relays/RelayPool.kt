package ktnostr.nostr.relays

import ktnostr.nostr.RelayError

class RelayPool(){
    private lateinit var relayList: MutableList<Relay>

    constructor(relays: List<Relay>) : this() {
        this.relayList = relays as MutableList<Relay>
    }

    fun getRelays() = relayList.toList()

    fun addRelay(relay: Relay){
        if (relayList.add(relay))
            return
        else throw RelayError("The relay ${relay.relayURI} could not be added.")
    }

    fun removeRelay(relay: Relay){
        relayList.remove(relay)
    }
}


