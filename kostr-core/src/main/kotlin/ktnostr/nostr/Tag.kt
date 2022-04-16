package ktnostr.nostr

/**
 * The model representing the event tag. The event tag carries
 * information such as what identifies the tag('p', 'e', etc), the tag's
 * content, or description(a referenced pubkey, event, etc), and
 * optionally a recommended relay url, which you can add to the list
 * of relays you already have.
 *
 * @param identifier The tag identifier, as a string
 * @param description The tag's contents, as a string
 * @param recommendedRelayUrl (optional) A recommended relay url, as a string
 */
data class Tag(val identifier: String, val description: String,
          val recommendedRelayUrl: String? = null)

/**
 * Transforms a list of tags to a list of string arrays.
 * This function is necessary in order for the tag structure to
 * be serialized correctly.
 *
 * @return An array of string arrays
 */
fun List<Tag>.toStringList(): List<List<String>> {
    val tagStringList: MutableList<List<String>> = mutableListOf()
    this.forEach { tag ->
        val elementList: List<String> = if (tag.recommendedRelayUrl != null){
            listOf(tag.identifier, tag.description, tag.recommendedRelayUrl)
        } else {
            listOf(tag.identifier, tag.description)
        }
        tagStringList.add(elementList)
    }

    return tagStringList.toList()
}