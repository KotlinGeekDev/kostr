
import com.fasterxml.jackson.module.kotlin.readValue
import ktnostr.nostr.eventMapper
import ktnostr.nostr.relays.RelayMessage
import ktnostr.nostr.relays.RelayNotice
import org.junit.jupiter.api.Test

class RelayMessageTests {

    @Test
    fun `the relay event message is correctly parsed`(){
        val relayNoticeJson = """["NOTICE", "You are not allowed to publish to this relay"]"""
        val relayNotice: RelayMessage = eventMapper.readValue<RelayNotice>(relayNoticeJson)
        val correctRelayNotice = RelayNotice("NOTICE",
            "You are not allowed to publish to this relay")
        val correctRelayNoticeJson = eventMapper.writeValueAsString(correctRelayNotice)
        println(correctRelayNoticeJson)
        println(relayNotice)
        println(correctRelayNotice)
        assert(relayNotice == correctRelayNotice)

    }

}