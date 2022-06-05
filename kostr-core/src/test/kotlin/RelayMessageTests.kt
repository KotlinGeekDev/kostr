import com.fasterxml.jackson.module.kotlin.readValue
import ktnostr.nostr.relays.RelayEventMessage
import ktnostr.nostr.relays.RelayMessage
import ktnostr.nostr.relays.RelayNotice
import org.junit.jupiter.api.Test

class RelayMessageTests {

    @Test
    fun `the relay message is correctly parsed to a notice`() {
        val relayNoticeJson = """["NOTICE", "You are not allowed to publish to this relay"]"""
        val relayNotice = testEventMapper.readValue<RelayMessage>(relayNoticeJson)
        val correctRelayNotice = RelayNotice(
            "NOTICE",
            "You are not allowed to publish to this relay"
        )
        val correctRelayNoticeJson = testEventMapper.writeValueAsString(correctRelayNotice)
        println(correctRelayNoticeJson)
        println(relayNotice)
        println(correctRelayNotice)
        assert(relayNotice == correctRelayNotice)

    }

    @Test
    fun `the relay message is correctly parsed to a relay message event`() {
        val relayMessageJson =
            """["EVENT","mySub",{"id":"f0d7e34a1f531c04fee5846ee85ae564e9e0ed389e82fd79c58f2bedca19b0e4","pubkey":"056ccc33d638633ecc60ee28db5f226ad3acfdfe69a73aa9670cc8beb0d9dc74","created_at":1640340342,"kind":0,"tags":[],"content":"{\"name\":\"Colby\",\"picture\":\"https://a57.foxnews.com/static.foxbusiness.com/foxbusiness.com/content/uploads/2020/12/0/0/Bitcoin-Gold.jpg?ve=1&tl=1\",\"about\":\"Testing\"}","sig":"f0fa211543b32adf6892ee5f18f85e70946dc1579fd390b04d12579d919d9afbb5195395e8b3ccb3c2d03fa21184c0935c322151b584152cbc3c89b2048f8442"}]"""
        val relayMessage = testEventMapper.readValue<RelayMessage>(relayMessageJson)
        val correctRelayMessage = RelayEventMessage(
            "EVENT",
            "mySub",
            "{\"id\":\"f0d7e34a1f531c04fee5846ee85ae564e9e0ed389e82fd79c58f2bedca19b0e4\",\"pubkey\":\"056ccc33d638633ecc60ee28db5f226ad3acfdfe69a73aa9670cc8beb0d9dc74\",\"created_at\":1640340342,\"kind\":0,\"tags\":[],\"content\":\"{\\\"name\\\":\\\"Colby\\\",\\\"picture\\\":\\\"https://a57.foxnews.com/static.foxbusiness.com/foxbusiness.com/content/uploads/2020/12/0/0/Bitcoin-Gold.jpg?ve=1&tl=1\\\",\\\"about\\\":\\\"Testing\\\"}\",\"sig\":\"f0fa211543b32adf6892ee5f18f85e70946dc1579fd390b04d12579d919d9afbb5195395e8b3ccb3c2d03fa21184c0935c322151b584152cbc3c89b2048f8442\"}"
        )
        val correctEventMessageJson = testEventMapper.writeValueAsString(correctRelayMessage)
        println(correctEventMessageJson)
        println(relayMessage)
        println(correctRelayMessage)
        assert(relayMessage == correctRelayMessage)
    }

}