
import ktnostr.nostr.EventKind
import ktnostr.nostr.NostrFilter
import org.junit.Test
import kotlin.test.assertEquals

class NostrFilterTest {

    // For the first filter
    private val eventIdList = listOf("event_id_1", "event_id_2", "event_id_3")
    private val authorList = listOf("author_pubkey_1", "author_pubkey_2")
    private val listOfKinds = listOf(EventKind.TEXT_NOTE)
    private val referencedEventIds = listOf("ref_event_id_1", "ref_event_id_2")
    private val referencedProfiles = listOf("ref_pubkey_1")
    private val upperTimeLimit = 1653822739.toLong()
    private val lowerTimeLimit = upperTimeLimit - 24 * 60 * 60
    private val maxEventLimit = 25
    val filter = NostrFilter(eventIdList, authorList, listOfKinds, referencedEventIds,
        referencedProfiles, lowerTimeLimit, upperTimeLimit, maxEventLimit)

    // For the second filter
    private val secondEventIdList = listOf("event_id_4", "event_id_5")
    private val secondAuthorList = listOf("author_pubkey_3", "author_pubkey_4")
    private val secondKindList = listOf(EventKind.METADATA, EventKind.RELAY_RECOMMENDATION)
    private val referencedEventIdList = listOf("ref_event_id_3", "ref_event_id_4")
    private val referencedProfileList = listOf("ref_pubkey_2", "ref_pubkey_3", "ref_pubkey_4")
    private val secondMaxEventLimit = 10
    val filter_2 = NostrFilter(secondEventIdList, secondAuthorList, secondKindList, referencedEventIdList,
        referencedProfileList, lowerTimeLimit, upperTimeLimit, secondMaxEventLimit)

    @Test
    fun `it serializes the nostr filter correctly`(){
        val correctFilterJson = """{"ids":["event_id_1","event_id_2","event_id_3"],"authors":["author_pubkey_1","author_pubkey_2"],"kinds":[1],"#e":["ref_event_id_1","ref_event_id_2"],"#p":["ref_pubkey_1"],"since":1653736339,"until":1653822739,"limit":25}"""

        val filterJson = testEventMapper.writeValueAsString(filter)
        println("Correct filterJson: \n $correctFilterJson")
        println("Generated filterJson: \n $filterJson")
        assertEquals(filterJson, correctFilterJson)

    }

}