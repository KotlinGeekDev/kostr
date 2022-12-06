package ktnostr.nostr

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EventTests {
    val testEventMapper = jacksonObjectMapper()

    @Test
    fun `it generates the correct raw event json for obtaining the eventId`() {
        val someTags = listOf(
            Tag("#p", "42365g3ghgf7gg15hj64jk", null)
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

    @Test
    fun `it generates correct events with multiple tags`() {
//        val eventTestTag = listOf(
//            Tag("p", "13adc511de7e1cfcf1c6b7f6365fb5a03442d7bcacf565ea57fa7770912c023d"),
//        )
//        val rawEventTestData = listOf("f86c44a2de95d9149b51c6a29afeabba264c18e2fa7c49de93424a0c56947785",
//            "1640839235", "4", "uRuvYr585B80L6rSJiHocw==?iv=oh6LVqdsYYol3JfFnXTbPA==",
//            "a5d9290ef9659083c490b303eb7ee41356d8778ff19f2f91776c8dc4443388a64ffcf336e61af4c25c05ac3ae952d1ced889ed655b67790891222aaa15b99fdd")
//        val eventId = getEventId(rawEventTestData[0], rawEventTestData[1].toLong(),
//            rawEventTestData[2].toInt(), eventTestTag, rawEventTestData[3])
//        val testEvent = Event(eventId, rawEventTestData[0], rawEventTestData[1].toLong(),
//            rawEventTestData[2].toInt(), eventTestTag, rawEventTestData[3], rawEventTestData[4])
//        val eventJson = testEvent.serialize()
//
//        println(eventJson)
//        assert(true)
    }
}