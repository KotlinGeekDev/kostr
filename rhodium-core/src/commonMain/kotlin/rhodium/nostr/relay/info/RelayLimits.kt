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

package rhodium.nostr.relay.info

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a relay's advertised limits.
 * For more, see [NIP-11](https://github.com/nostr-protocol/nips/blob/master/11.md).
 * All properties here are *optional*.
 *
 * @property maxMessageLength - The maximum length of the event to be published, in bytes.
 * @property maxSubscriptions - The maximum number of active subscriptions on a single connection to the relay.
 * @property maxLimit - The maximum limit for subscription filters sent to this relay.
 * @property maxSubscriptionIdLength - The maximum length of the subscription id string.
 * @property maxEventTagNumber - The maximum number of tags allowed in an event published to this relay.
 * @property maxContentLength - The maximum number of characters in the content field of an event to be published
 * to this relay.
 * @property minPowDifficulty - The minimum amount of PoW difficulty needed for an event to be published to this
 * relay.
 * @property isAuthRequired - Determines if the relay has placed limits on publishing events/sending requests.
 * @property creationDateLowerLimit - Determines the 'lowest date', or date furthest back in time,
 * that an event being published(or request being made) can reach.
 * @property creationDateUpperLimit - Determines the 'highest date', or date furthest into the future, that
 * an event being published(or request limit being made) to the relay can reach.
 * @property defaultLimit - The limit being applied by default, when a request is sent without any limits.
 */
@Serializable
data class RelayLimits(
    @SerialName("max_message_length") val maxMessageLength: Int? = null,
    @SerialName("max_subscriptions") val maxSubscriptions: Int? = null,
    @SerialName("max_limit") val maxLimit: Int? = null,
    @SerialName("max_subid_length") val maxSubscriptionIdLength: Int? = null,
    @SerialName("max_event_tags") val maxEventTagNumber: Int? = null,
    @SerialName("max_content_length") val maxContentLength: Int? = null,
    @SerialName("min_pow_difficulty") val minPowDifficulty: Int? = null,
    @SerialName("auth_required") val isAuthRequired: Boolean? = null,
    @SerialName("payment_required") val isPaymentRequired: Boolean? = null,
    @SerialName("restricted_writes") val writesAreRestricted: Boolean? = null,
    @SerialName("created_at_lower_limit") val creationDateLowerLimit: Long? = null,
    @SerialName("created_at_upper_limit") val creationDateUpperLimit: Long? = null,
    @SerialName("default_limit") val defaultLimit: Int? = null
)

