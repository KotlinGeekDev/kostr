package ktnostr.nostr

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NostrFilter(
    @SerialName("ids") val listOfIds: List<String>? = null,
    @SerialName("authors") val authorsList: List<String>? = null,
    @SerialName("kinds") val listOfKinds: List<Int>,
    @SerialName("#e") val eventIdList: List<String>? = null,
    @SerialName("#p") val pubkeyList: List<String>? = null,
    val since: Long? = null,
    val until: Long? = null,
    val limit: Int = 1
)