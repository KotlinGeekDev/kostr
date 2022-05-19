
import ktnostr.nostr.Tag
import ktnostr.nostr.rawEventJson0
import org.junit.jupiter.api.Test

class EventTests {

    @Test
    fun `it generates the correct raw event json for obtaining the eventId`(){
        val someTags = listOf(
            Tag("#p", "42365g3ghgf7gg15hj64jk", null)
            //Triple("#e", "546454ghgfnfg56456fgngg", "wss://relayer.fiatjaf.com")
        )
        println("Test 1:")
        val rawEventData = listOf("8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f",
                "1649108200", "1", "Testing some event")
        val rawEventInJson = rawEventJson0(rawEventData[0], rawEventData[1].toLong(), rawEventData[2].toInt(),
                            someTags, rawEventData[3])
        val correctRawJson = "[0,\"8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f\",1649108200,1,[[\"#p\",\"42365g3ghgf7gg15hj64jk\"]],\"Testing some event\"]"
        println(rawEventInJson)
        assert(rawEventInJson == correctRawJson)

    }


}