import ktnostr.nostr.relays.RelayNotice
import ktnostr.nostr.relays.toRelayMessage
import org.junit.jupiter.api.Test

class RelayMessageTests {

    @Test
    fun `the relay event message is correctly parsed`(){
        val relayNoticeJson = "[\"NOTICE\", \"You are not allowed to publish to this relay\"]"
        val relayNotice = relayNoticeJson.toRelayMessage()
        val correctRelayNotice = RelayNotice("NOTICE",
            "You are not allowed to publish to this relay")
        println(relayNotice)
        println(correctRelayNotice)
        assert(relayNotice == correctRelayNotice)

    }

}