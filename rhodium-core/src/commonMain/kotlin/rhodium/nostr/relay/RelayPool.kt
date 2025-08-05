/*
 * MIT License
 *
 * Copyright (c) 2025 KotlinGeekDev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package rhodium.nostr.relay

import rhodium.nostr.RelayError
import kotlin.jvm.JvmStatic

/**
 * Handles the relays used by the `NostrService`.
 * For now, it is very simple. It just handles addition,
 * removal, and resetting of the relay pool.
 *
 * You can specify a custom list of relays by using the `Relay(...)` primary constructor,
 * or using [RelayPool.fromUrls].
 */
class RelayPool {

    private val relayList: MutableList<Relay> = mutableListOf()
    constructor(){
        getDefaultRelays().forEach {
            relayList.add(it)
        }
    }

    constructor(relays: List<Relay>) : this() {
        relays.forEach { relayList.add(it) }
    }

    fun getRelays() = relayList.toList()

    fun addRelay(relay: Relay) {
        if (relayList.add(relay))
            return
        else throw RelayError("The relay ${relay.relayURI} could not be added.")
    }

    fun addRelays(vararg relayUrls: String){
        relayUrls.forEach { url ->
            addRelay(Relay(url))
        }
    }

    fun addRelays(relays: Collection<Relay>) {
        relays.forEach { relayList.add(it) }
    }

    fun addRelayList(listOfRelays: Collection<String>) {
        val relayRefs = listOfRelays.map { Relay(it) }
        addRelays(relayRefs)
    }

    fun removeRelay(relay: Relay) {
        relayList.remove(relay)
    }

    fun clearPool() {
        relayList.clear()
    }

    companion object {

        fun fromUrls(vararg relayUris: String): RelayPool {
            val relayList = relayUris.map { Relay(it) }
            return RelayPool(relayList)
        }

        fun fromUrls(urlList: Collection<String>): RelayPool {
            val relayList = urlList.map { Relay(it) }
            return RelayPool(relayList)
        }

        @JvmStatic
        fun getDefaultRelays(): List<Relay> = listOf(
            Relay("wss://nostr-pub.wellorder.net"),
            Relay("wss://relay.damus.io"),
            Relay("wss://relay.nostr.wirednet.jp"),
            Relay("wss://relay.nostr.band"),
        )
    }

}


