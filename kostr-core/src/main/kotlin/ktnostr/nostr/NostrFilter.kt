package ktnostr.nostr

import com.fasterxml.jackson.annotation.JsonProperty

class NostrFilter(@JsonProperty("ids") val listOfIds: List<String>,
                  @JsonProperty("authors") val authorsList: List<String>,
                  @JsonProperty("kinds") val listOfKinds: List<Int>,
                  @JsonProperty("#e") val eventIdList: List<String>,
                  @JsonProperty("#p") val pubkeyList: List<String>,
                  val since: Long,
                  val until: Long,
                  val limit: Int )