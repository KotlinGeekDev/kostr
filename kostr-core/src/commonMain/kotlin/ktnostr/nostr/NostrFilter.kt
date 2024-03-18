package ktnostr.nostr

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class NostrFilter private constructor(
    @SerialName("ids") private val listOfIds: List<String>? = null,
    @SerialName("authors") private val authorsList: List<String>? = null,
    @SerialName("kinds") private val listOfKinds: List<Int>,
    @SerialName("#e") private val eventIdList: List<String>? = null,
    @SerialName("#p") private val pubkeyList: List<String>? = null,
    @SerialName("#t") private val topicList: List<String>? = null,
    private val since: Long? = null,
    private val until: Long? = null,
    private val limit: Int = 1
) {

    override fun toString() = """
        Ids:$listOfIds
        Authors:$authorsList
        Kinds:$listOfKinds
        Tags
          Id:$eventIdList
          Pubkey:$pubkeyList
          Topic:$topicList
        Since:$since
        Until:$until
        Limit:$limit  
    """.trimIndent()

    companion object {
        fun newFilter() = Builder()
    }

    class Builder {
        private var listOfIds: List<String>? = null
        private var authorsList: List<String>? = null
        private var listOfKinds: List<Int> = emptyList()
        private var eventTagList: List<String>? = null
        private var pubkeyTagList:List<String>? = null
        private var topicList: List<String>? = null
        private var since: Long? = null
        private var until: Long? = null
        private var limit: Int = 1

        fun idList(iDList: List<String>? = null) = apply {
            listOfIds = iDList
        }

        fun authors(authorList: List<String>? = null) = apply {
            authorsList = authorList
        }

        fun kinds(kindList: List<Int>) = apply {
            listOfKinds = kindList
        }

        fun eventTagList(listOfEventTags: List<String>? = null) = apply {
            eventTagList = listOfEventTags
        }

        fun pubkeyTagList(pubkeyList: List<String>? = null) = apply {
            pubkeyTagList = pubkeyList
        }

        fun topics(listOfTopics: List<String>? = null) = apply {
            topicList = listOfTopics
        }

        fun since(timeStamp: Long? = null) = apply {
            since = timeStamp
        }

        fun until(timeStamp: Long? = null) = apply {
            until = timeStamp
        }

        fun limit(receivingEventLimit: Int) = apply {
            limit = receivingEventLimit
        }

        fun build() = NostrFilter(
            listOfIds = listOfIds,
            authorsList = authorsList,
            listOfKinds = listOfKinds,
            eventIdList = eventTagList,
            pubkeyList = pubkeyTagList,
            topicList = topicList,
            since = since,
            until = until,
            limit = limit
        )
    }
}