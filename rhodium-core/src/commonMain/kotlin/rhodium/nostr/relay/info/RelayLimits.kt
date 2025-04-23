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

