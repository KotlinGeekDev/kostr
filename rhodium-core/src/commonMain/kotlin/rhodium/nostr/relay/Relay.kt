package rhodium.nostr.relay

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import rhodium.net.httpClient
import rhodium.nostr.RelayInfoFetchError
import rhodium.nostr.eventMapper
import rhodium.nostr.relay.info.RelayLimits

class Relay(
    val relayURI: String,
    val readPolicy: Boolean = true,
    val writePolicy: Boolean = true
) {
    companion object {
        fun fromUrl(address: String): Relay {
            return Relay(address)
        }

        suspend fun fetchInfoFor(relayUrl: String, httpClient: HttpClient = httpClient()): Info {
            val raw = StringBuilder(relayUrl).removePrefix("wss://").removePrefix("ws://")
            val actualUrl = "https://$raw"
            val relayInfoResponse = httpClient.get(actualUrl) {
                headers.append("Accept", "application/nostr+json")
            }

            if (relayInfoResponse.status.isSuccess()) {
                return infoFromJson(relayInfoResponse.bodyAsText())
            }
            else throw RelayInfoFetchError("Could not fetch relay info with reason: ${relayInfoResponse.bodyAsText()}")
        }

        fun infoFromJson(relayInfoJson: String): Info {
            val relayInfo = eventMapper.decodeFromString<Info>(relayInfoJson)
            return relayInfo
        }
    }

    override fun toString(): String {
        return "Relay(url=$relayURI, read=$readPolicy, write=$writePolicy)"
    }

    //Docs: Write something useful about Relay Info
    @Serializable
    class Info(
        val name: String = "",
        val description: String = "",
        val banner: String = "",
        val icon: String = "",
        val pubkey: String = "",
        val contact: String = "",
        @SerialName("supported_nips") val supportedNips: IntArray = emptyArray<Int>().toIntArray(),
        @SerialName("software") val relaySoftware: String = "",
        @SerialName("version") val softwareVersion: String = "",
        //Extra fields below
        @SerialName("limitation") val limits: RelayLimits,
    )
}