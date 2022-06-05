import ktnostr.nostr.EventKind
import ktnostr.nostr.NostrFilter
import ktnostr.nostr.client.RequestMessage
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import kotlin.test.assertEquals

data class TV(val nostrFilters: List<NostrFilter>, val nostrFilterJson: String)

@RunWith(Parameterized::class)
class ClientMessageTests {
    @Parameter
    lateinit var tv: TV

    @Test
    fun requestMessageTest() {
        val requestMessage =
            RequestMessage(subscriptionId = "mySub", filters = tv.nostrFilters)
        val requestJson = testEventMapper.writeValueAsString(requestMessage)
        assertEquals(tv.nostrFilterJson, requestJson)
    }

    companion object {
        private val filterOne = NostrFilter(
                    listOfIds = listOf("event_id_1", "event_id_2", "event_id_3"),
                    authorsList = listOf("author_pubkey_1", "author_pubkey_2"),
                    listOfKinds = listOf(EventKind.TEXT_NOTE),
                    eventIdList = listOf("ref_event_id_1", "ref_event_id_2"),
                    pubkeyList = listOf("ref_pubkey_1"),
                    since = 1653822739L - 24 * 60 * 60,
                    until = 1653822739L,
                    limit = 25
                )
        private val filterTwo = NostrFilter(
                    listOfIds = listOf("event_id_4", "event_id_5"),
                    authorsList = listOf("author_pubkey_3", "author_pubkey_4"),
                    listOfKinds = listOf(EventKind.METADATA, EventKind.RELAY_RECOMMENDATION),
                    eventIdList = listOf("ref_event_id_3", "ref_event_id_4"),
                    pubkeyList = listOf("ref_pubkey_2", "ref_pubkey_3", "ref_pubkey_4"),
                    since = 1653822739L - 24 * 60 * 60,
                    until = 1653822739L,
                    limit = 10
                )
        @JvmStatic
        @Parameters
        fun testVectors() = listOf(
            TV(listOf(filterOne), """["REQ","mySub",{"ids":["event_id_1","event_id_2","event_id_3"],"authors":["author_pubkey_1","author_pubkey_2"],"kinds":[1],"#e":["ref_event_id_1","ref_event_id_2"],"#p":["ref_pubkey_1"],"since":1653736339,"until":1653822739,"limit":25}]"""),
            TV(listOf(filterTwo), """["REQ","mySub",{"ids":["event_id_4","event_id_5"],"authors":["author_pubkey_3","author_pubkey_4"],"kinds":[0,2],"#e":["ref_event_id_3","ref_event_id_4"],"#p":["ref_pubkey_2","ref_pubkey_3","ref_pubkey_4"],"since":1653736339,"until":1653822739,"limit":10}]"""),
            TV(listOf(filterOne, filterTwo), """["REQ","mySub",{"ids":["event_id_1","event_id_2","event_id_3"],"authors":["author_pubkey_1","author_pubkey_2"],"kinds":[1],"#e":["ref_event_id_1","ref_event_id_2"],"#p":["ref_pubkey_1"],"since":1653736339,"until":1653822739,"limit":25},{"ids":["event_id_4","event_id_5"],"authors":["author_pubkey_3","author_pubkey_4"],"kinds":[0,2],"#e":["ref_event_id_3","ref_event_id_4"],"#p":["ref_pubkey_2","ref_pubkey_3","ref_pubkey_4"],"since":1653736339,"until":1653822739,"limit":10}]""")
        )
    }
}
