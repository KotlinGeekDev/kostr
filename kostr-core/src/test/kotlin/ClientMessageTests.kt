import ktnostr.nostr.EventKind
import ktnostr.nostr.NostrFilter
import ktnostr.nostr.client.RequestMessage
import org.junit.Test
import kotlin.test.assertEquals

class ClientMessageTests {

    // For the first filter
    private val eventIdList = listOf("event_id_1", "event_id_2", "event_id_3")
    private val authorList = listOf("author_pubkey_1", "author_pubkey_2")
    private val listOfKinds = listOf(EventKind.TEXT_NOTE)
    private val referencedEventIds = listOf("ref_event_id_1", "ref_event_id_2")
    private val referencedProfiles = listOf("ref_pubkey_1")
    private val upperTimeLimit = 1653822739.toLong()
    private val lowerTimeLimit = upperTimeLimit - 24 * 60 * 60
    private val maxEventLimit = 25
    private val filter = NostrFilter(eventIdList, authorList, listOfKinds, referencedEventIds,
        referencedProfiles, lowerTimeLimit, upperTimeLimit, maxEventLimit)

    // For the second filter
    private val secondEventIdList = listOf("event_id_4", "event_id_5")
    private val secondAuthorList = listOf("author_pubkey_3", "author_pubkey_4")
    private val secondKindList = listOf(EventKind.METADATA, EventKind.RELAY_RECOMMENDATION)
    private val referencedEventIdList = listOf("ref_event_id_3", "ref_event_id_4")
    private val referencedProfileList = listOf("ref_pubkey_2", "ref_pubkey_3", "ref_pubkey_4")
    private val secondMaxEventLimit = 10
    private val secondFilter = NostrFilter(secondEventIdList, secondAuthorList, secondKindList, referencedEventIdList,
        referencedProfileList, lowerTimeLimit, upperTimeLimit, secondMaxEventLimit)

    @Test
    fun `the request message for the first filter is correctly serialized`(){
        val correctRequestJson = """["REQ","mySub",{"ids":["event_id_1","event_id_2","event_id_3"],"authors":["author_pubkey_1","author_pubkey_2"],"kinds":[1],"#e":["ref_event_id_1","ref_event_id_2"],"#p":["ref_pubkey_1"],"since":1653736339,"until":1653822739,"limit":25}]"""

        val requestMessage = RequestMessage(subscriptionId = "mySub", filters = listOf(filter))
        val requestJson = testEventMapper.writeValueAsString(requestMessage)
        println("#1. Correct requestJson: \n $correctRequestJson")
        println("#1. Generated requestJson: \n $requestJson")
        assertEquals(correctRequestJson, requestJson)
    }

    @Test
    fun `the request message for the second filter is correctly serialized`(){
        val correctRequestJson = """["REQ","mySub",{"ids":["event_id_4","event_id_5"],"authors":["author_pubkey_3","author_pubkey_4"],"kinds":[0,2],"#e":["ref_event_id_3","ref_event_id_4"],"#p":["ref_pubkey_2","ref_pubkey_3","ref_pubkey_4"],"since":1653736339,"until":1653822739,"limit":10}]"""

        val requestMessage = RequestMessage(subscriptionId = "mySub", filters = listOf(secondFilter))
        val requestJson = testEventMapper.writeValueAsString(requestMessage)
        println("#2. Correct requestJson: \n $correctRequestJson")
        println("#2. Generated requestJson: \n $requestJson")
        assertEquals(correctRequestJson, requestJson)
    }

    @Test
    fun `the request with several filters is correctly serialized`(){
        val inlinedRequestJson = """["REQ","mySub",{"ids":["event_id_1","event_id_2","event_id_3"],"authors":["author_pubkey_1","author_pubkey_2"],"kinds":[1],"#e":["ref_event_id_1","ref_event_id_2"],"#p":["ref_pubkey_1"],"since":1653736339,"until":1653822739,"limit":25},
            |{"ids":["event_id_4","event_id_5"],"authors":["author_pubkey_3","author_pubkey_4"],"kinds":[0,2],"#e":["ref_event_id_3","ref_event_id_4"],"#p":["ref_pubkey_2","ref_pubkey_3","ref_pubkey_4"],"since":1653736339,"until":1653822739,"limit":10}]""".trimMargin()
        //The extra variable below is used just for replacing the '\n' delimiter. It is just for testing. Not for production.
        val correctRequestJson = inlinedRequestJson.replace("\n", "")
        println(correctRequestJson)
        val requestMessage = RequestMessage(subscriptionId = "mySub", filters = listOf(filter, secondFilter))
        val requestMessageJson = testEventMapper.writeValueAsString(requestMessage)

        assertEquals(correctRequestJson, requestMessageJson)
    }

}