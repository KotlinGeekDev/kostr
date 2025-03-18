/*
 * MIT License
 *
 * Copyright (c) 2025 KotlinGeekDev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

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
import rhodium.nostr.relay.info.Payments
import rhodium.nostr.relay.info.RelayLimits
import rhodium.nostr.relay.info.RetentionPolicy

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
    data class Info(
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
        @SerialName("limitation") val limits: RelayLimits? = null,
        @SerialName("retention") val retentionPolicies: Array<RetentionPolicy> = emptyArray<RetentionPolicy>(),
        //Extra field: Content Limitation
        @SerialName("relay_countries") val relayRegionHosts: Array<String>? = null,
        //Extra field group: Community Preferences
        //TODO: Extract field group into separate class, and use custom serializer for Info.
        @SerialName("language_tags") val allowedLanguages: Array<String>? = null,
        @SerialName("tags") val allowedTopics: Array<String>? = null,
        @SerialName("posting_policy") val postingPolicy: String? = null,
        //Extra field group: Pay-to-Relay
        @SerialName("payments_url") val paymentUrl: String? = null,
        @SerialName("fees") val paymentInfo: Payments? = null
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Info

            if (name != other.name) return false
            if (description != other.description) return false
            if (banner != other.banner) return false
            if (icon != other.icon) return false
            if (pubkey != other.pubkey) return false
            if (contact != other.contact) return false
            if (!supportedNips.contentEquals(other.supportedNips)) return false
            if (relaySoftware != other.relaySoftware) return false
            if (softwareVersion != other.softwareVersion) return false
            if (limits != other.limits) return false
            if (!retentionPolicies.contentEquals(other.retentionPolicies)) return false
            if (!relayRegionHosts.contentEquals(other.relayRegionHosts)) return false
            if (!allowedLanguages.contentEquals(other.allowedLanguages)) return false
            if (!allowedTopics.contentEquals(other.allowedTopics)) return false
            if (postingPolicy != other.postingPolicy) return false
            if (paymentUrl != other.paymentUrl) return false
            if (paymentInfo != other.paymentInfo) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + description.hashCode()
            result = 31 * result + banner.hashCode()
            result = 31 * result + icon.hashCode()
            result = 31 * result + pubkey.hashCode()
            result = 31 * result + contact.hashCode()
            result = 31 * result + supportedNips.contentHashCode()
            result = 31 * result + relaySoftware.hashCode()
            result = 31 * result + softwareVersion.hashCode()
            result = 31 * result + (limits?.hashCode() ?: 0)
            result = 31 * result + retentionPolicies.contentHashCode()
            result = 31 * result + (relayRegionHosts?.contentHashCode() ?: 0)
            result = 31 * result + (allowedLanguages?.contentHashCode() ?: 0)
            result = 31 * result + (allowedTopics?.contentHashCode() ?: 0)
            result = 31 * result + (postingPolicy?.hashCode() ?: 0)
            result = 31 * result + (paymentUrl?.hashCode() ?: 0)
            result = 31 * result + (paymentInfo?.hashCode() ?: 0)
            return result
        }
    }
}