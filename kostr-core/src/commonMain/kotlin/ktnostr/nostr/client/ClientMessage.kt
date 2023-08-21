@file:Suppress("SERIALIZER_TYPE_INCOMPATIBLE")

package ktnostr.nostr.client

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import ktnostr.nostr.Event
import ktnostr.nostr.NostrFilter
import ktnostr.nostr.StringArraySerializer

@Serializable
sealed class ClientMessage(open val messageType: String)

//@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@Serializable(with = StringArraySerializer::class)
data class ClientEventMessage(
    override val messageType: String = "EVENT",
    val event: Event
) : ClientMessage(messageType)

@Serializable(with = StringArraySerializer::class)
data class RequestMessage(
    override val messageType: String = "REQ",
    val subscriptionId: String,
    @Serializable(with = FilterListSerializer::class)
    val filters: List<NostrFilter>?
) : ClientMessage(messageType)

@Serializable(with = StringArraySerializer::class)
data class CloseRequest(
    override val messageType: String = "CLOSE",
    val subscriptionId: String
) : ClientMessage(messageType)



class FilterListSerializer : KSerializer<List<NostrFilter>> {
    val default = ListSerializer(elementSerializer = NostrFilter.serializer())
    override val descriptor: SerialDescriptor
        get() = default.descriptor

    override fun serialize(encoder: Encoder, value: List<NostrFilter>) {
        encoder.encodeCollection(descriptor, value, { index, filter ->  })
    }

    override fun deserialize(decoder: Decoder): List<NostrFilter> {
        val filterList = decoder.decodeSerializableValue(default)
        return filterList
    }
//    override fun serialize(
//        value: List<NostrFilter>?,
//        gen: JsonGenerator?,
//        serializers: SerializerProvider?
//    ) {
//        value?.forEach { filter ->
//            gen?.writeObject(filter)
//        }
//    }

}