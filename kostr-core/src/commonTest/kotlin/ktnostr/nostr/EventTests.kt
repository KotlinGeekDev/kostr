package ktnostr.nostr

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class EventTests {
    private val testEventMapper = Json

    @Test
    fun `it generates the correct raw event json for obtaining the eventId`() {
        val someTags = listOf(
            Tag("#p", "42365g3ghgf7gg15hj64jk")
            //Triple("#e", "546454ghgfnfg56456fgngg", "wss://relayer.fiatjaf.com")
        )
        println("Test 1:")
        val rawEventData = listOf(
            "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
            "1649108200", "1", "Testing some event"
        )
        val rawEventInJson = rawEventJson0(
            rawEventData[0], rawEventData[1].toLong(), rawEventData[2].toInt(),
            someTags, rawEventData[3]
        )
        val correctRawJson =
            "[0,\"8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f\",1649108200,1,[[\"#p\",\"42365g3ghgf7gg15hj64jk\"]],\"Testing some event\"]"
        println(rawEventInJson)
        assertEquals(rawEventInJson, correctRawJson)

    }

//    @Test
//    fun `it generates correct events with multiple tags`() {
//        assert(true)
//    }

    @Test
    fun `testing the equivalence of event ids during event generation`(){
        val secKeyHex = "6ba903b7888191180a0959a6d286b9d0719d33a47395c519ba107470412d2069"
        val pubKeyHex = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f"

        val profileEvent = Events.MetadataEvent(secKeyHex, pubKeyHex, profile = "Name.", timeStamp = 1640839235)
        val correspondingEvent = Events.generateEvent(EventKind.METADATA.kind, emptyList(),
            "Name.", secKeyHex, pubKeyHex, timeStamp = 1640839235)
        println("Profile Ev: $profileEvent")
        println("Corr. Ev: $correspondingEvent")
        assertEquals(profileEvent.id, correspondingEvent.id)
    }
}