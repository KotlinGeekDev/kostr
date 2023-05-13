package ktnostr.nostr

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(value = JsonInclude.Include.NON_NULL)
data class NostrFilter(
    @JsonProperty("ids") val listOfIds: List<String>? = null,
    @JsonProperty("authors") val authorsList: List<String>? = null,
    @JsonProperty("kinds") val listOfKinds: List<Int>,
    @JsonProperty("#e") val eventIdList: List<String>? = null,
    @JsonProperty("#p") val pubkeyList: List<String>? = null,
    val since: Long? = null,
    val until: Long? = null,
    val limit: Int = 1
)