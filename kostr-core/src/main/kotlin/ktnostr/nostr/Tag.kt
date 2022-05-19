//@file:JvmName("Tag")
package ktnostr.nostr

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize

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
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
//@JsonFormat(shape = JsonFormat.Shape.ARRAY)
//---To uncomment these when Issue #563 on jackson-module-kotlin is solved.
@JsonSerialize(using = TagSerializer::class)
data class Tag(val identifier: String, val description: String,
               val recommendedRelayUrl: String? = null)

class TagSerializer : JsonSerializer<Tag>() {
    override fun serialize(value: Tag?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeStartArray()
        gen?.writeString(value?.identifier)
        gen?.writeString(value?.description)
        if (value?.recommendedRelayUrl != null){ gen?.writeString(value.recommendedRelayUrl)}

        gen?.writeEndArray()
    }

}

//----Kept for legacy purposes, or when serialization above does not work----
///**
// * Transforms a list of tags to a list of string arrays.
// * This function is necessary in order for the tag structure to
// * be serialized correctly.
// *
// * @return An array of string arrays
// */
//fun List<Tag>.toStringList(): List<List<String>> {
//    val tagStringList: MutableList<List<String>> = mutableListOf()
//    this.forEach { tag ->
//        val elementList: List<String> = if (tag.recommendedRelayUrl != null){
//            listOf(tag.identifier, tag.description, tag.recommendedRelayUrl)
//        } else {
//            listOf(tag.identifier, tag.description)
//        }
//        tagStringList.add(elementList)
//    }
//
//    return tagStringList.toList()
//}