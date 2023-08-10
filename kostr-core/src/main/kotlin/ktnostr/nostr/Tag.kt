//@file:JvmName("Tag")
package ktnostr.nostr

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.ObjectCodec
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.node.ArrayNode

/**
 * The model representing the event tag. The event tag carries
 * information such as what identifies the tag('p', 'e', etc.), the tag's
 * content, or description(a referenced pubkey, event, etc),
 * a recommended relay url(optional), which you can add to the list
 * of relays you already have, and a petname or username(optional),
 * when a tag contains an identity's alias(or username).
 *
 * @param identifier The tag identifier, as a string
 * @param description The tag's contents, as a string
 * @param recommendedRelayUrl (optional) A recommended relay url, as a string
 */
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
//@JsonFormat(shape = JsonFormat.Shape.ARRAY)
//---To uncomment these when Issue #563 on jackson-module-kotlin is solved.
@JsonSerialize(using = Tag.TagSerializer::class)
@JsonDeserialize(using = Tag.TagDeserializer::class)
data class Tag(
    val identifier: String, val description: String,
    val recommendedRelayUrl: String? = null,
    val petname: String? = null
) {

    class TagSerializer : JsonSerializer<Tag>() {
        override fun serialize(value: Tag?, gen: JsonGenerator, serializers: SerializerProvider?) {
            gen.writeStartArray()
            gen.writeString(value?.identifier)
            gen.writeString(value?.description)
            if (value?.recommendedRelayUrl != null) {
                gen.writeString(value.recommendedRelayUrl)
            }
            if (value?.petname != null) {
                gen.writeString(value.petname)
            }
            gen.writeEndArray()
        }
    }

    class TagDeserializer : JsonDeserializer<Tag>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Tag {
            val codec: ObjectCodec = p.codec
            val kv = (codec.readTree(p) as ArrayNode).map { (it as JsonNode).asText() }
            val listSize = kv.size
            return when {
                listSize > 4 || listSize < 2 -> throw java.lang.Exception("Incorrect tag format.")
                listSize == 4 -> Tag(kv[0], kv[1], kv[2], kv[3])
                listSize == 3 -> Tag(kv[0], kv[1], kv[2])
                else -> Tag(kv[0], kv[1])
            }
        }
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
