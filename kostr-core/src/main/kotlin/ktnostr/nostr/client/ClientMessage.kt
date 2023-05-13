package ktnostr.nostr.client

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import ktnostr.nostr.Event
import ktnostr.nostr.NostrFilter

sealed class ClientMessage(open val messageType: String)

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class ClientEventMessage(
    override val messageType: String = "EVENT",
    val event: Event
) : ClientMessage(messageType)

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class RequestMessage(
    override val messageType: String = "REQ",
    val subscriptionId: String,
    @JsonSerialize(using = FilterListSerializer::class)
    val filters: List<NostrFilter>?
) : ClientMessage(messageType)

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
data class CloseRequest(
    override val messageType: String = "CLOSE",
    val subscriptionId: String
) : ClientMessage(messageType)

class FilterListSerializer : JsonSerializer<List<NostrFilter>>() {
    override fun serialize(
        value: List<NostrFilter>?,
        gen: JsonGenerator?,
        serializers: SerializerProvider?
    ) {
        value?.forEach { filter ->
            gen?.writeObject(filter)
        }
    }

}