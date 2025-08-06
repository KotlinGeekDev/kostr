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

/**
 * Represents a Nostr relay, including it's read/write policies.
 * When creating you can use the `Relay(...)` constructor, or use
 * [fromUrl], and just pass in the relay address.
 *
 * The read/write policies are set to true by default.
 *
 * @constructor Relay(String, Boolean, Boolean)
 *
 * @property relayURI - The relay's address, as a String
 * @property readPolicy - The relay's read status(whether a relay accepts reads from it), as a Boolean.
 * @property writePolicy - The relay's write status(whether a relay accepts writes to it), as a Boolean.
 */
class Relay(
    val relayURI: String,
    val readPolicy: Boolean = true,
    val writePolicy: Boolean = true
) {
    companion object {
        fun fromUrl(address: String): Relay {
            return Relay(address)
        }

        /**
         * Fetches information about a relay, per [NIP-11](https://github.com/nostr-protocol/nips/blob/master/11.md),
         * and returns the information as an [Info] object.
         *
         * @param relayUrl - The relay URL/URI, as a String
         * @param httpClient - (Optional) A custom HTTP client that can be used for fetching the info,
         * particularly for a project using a particular client.
         * By default, the library's own [client][rhodium.net.httpClient] is used.
         *
         * @return Relay information, as an [Info] object.
         */
        suspend fun fetchInfoFor(
            relayUrl: String,
            httpClient: HttpClient = httpClient()
        ): Info {
            val raw = relayUrl.removePrefix("wss://").removePrefix("ws://")
            val actualUrl = "https://$raw"
            val relayInfoResponse = httpClient.get(actualUrl) {
                headers.append("Accept", "application/nostr+json")
            }

            if (relayInfoResponse.status.isSuccess()) {
                return infoFromJson(relayInfoResponse.bodyAsText())
            }
            else throw RelayInfoFetchError("Could not fetch relay info with reason: ${relayInfoResponse.bodyAsText()}")
        }

        /**
         * Transforms already obtained relay info in JSON form,
         * and returns it as an `Info` object.
         *
         * @param relayInfoJson - The relay info, in JSON format.
         *
         * @return Relay information, stored as an [Info] object.
         */
        fun infoFromJson(
            relayInfoJson: String
        ): Info {
            val relayInfo = eventMapper.decodeFromString<Info>(relayInfoJson)
            return relayInfo
        }

        /**
         * Does the exact opposite of [infoFromJson],
         * taking relay information stored in an `Info` object, and returns it in JSON format.
         *
         * @param relayInfo - The relay's information, as an `Info` object
         *
         * @return A JSON formatted `String` representation of the relay info.
         */
        fun infoToJson(
            relayInfo: Info
        ): String {
            val relayInfoJson = eventMapper.encodeToString(relayInfo)
            return relayInfoJson
        }
    }

    override fun toString(): String {
        return "Relay(url=$relayURI, read=$readPolicy, write=$writePolicy)"
    }

    /**
     * Represents a relay's information,
     * provided as per [NIP-11](https://github.com/nostr-protocol/nips/blob/master/11.md).
     * The elements required for a relay's details are its name, description, banner, icon,
     * pubkey, and contact. The other properties are optional.
     *
     * By default, an `Info` object is "empty".
     *
     * @property name - The relay's name
     * @property description - A description of the relay
     * @property banner - A banner for the relay, similar to a user profile's banner.
     * @property icon - An icon for the relay, similar to a user's profile picture.
     * @property pubkey - The Nostr pubkey of the relay's administrator.
     * @property contact - An alternative contact for the relay administrator.
     * @property supportedNips - (Optional) A list of Nostr specs(NIPs) supported by the relay.
     * @property relaySoftware - (Optional) The name of the underlying relay implementation used by this relay.
     * @property softwareVersion - (Optional) The version of the relay implementation currently in use by the relay.
     * @property privacyPolicy - (Optional) The privacy policy for this relay.
     * @property termsOfService - (Optional) The relay's terms of service.
     * @property limits - (Optional) A set of limits imposed by the relay, as a [RelayLimits] object.
     * @property retentionPolicies - (Optional) A set of data retention policies provided by the relay.
     * A retention policy is stored as a [RetentionPolicy] object.
     * @see RetentionPolicy
     *
     * @property relayRegionHosts - (Optional) Countries, regions, whose policies might affect the relay's hosted content.
     * @property allowedLanguages - (Optional) A relay's preference concerning the language of content to be published to it.
     * @property allowedTopics - (Optional) A set of allowed topics for discussion on this relay.
     * @property postingPolicy - (Optional) The relay's posting policy; a set of guidelines on content
     * to be published to the relay.
     * @property paymentUrl - (Optional) Indicates where you should pay the relay operator.
     * Usually for paid relays, or paid tiers on mixed relays.
     * @property paymentInfo - (Optional) Contains info on payments to make: what to pay for, how to pay, how much to pay, etc.
     * The information is stored as a [Payments] object.
     */
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
        @SerialName("privacy_policy") val privacyPolicy: String = "",
        @SerialName("terms_of_service") val termsOfService: String = "",
        //Extra fields below
        @SerialName("limitation") val limits: RelayLimits? = null,
        @SerialName("retention") val retentionPolicies: Array<RetentionPolicy>? = null,
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