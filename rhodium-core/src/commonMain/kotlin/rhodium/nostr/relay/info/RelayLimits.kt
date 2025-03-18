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
    @SerialName("max_message_length")val maxMessageLength: Int = 0,
    @SerialName("max_subscriptions")val maxSubscriptions: Int = 0,
    @SerialName("max_filters")val maxFilters: Int = 0,
    @SerialName("max_limit")val maxLimit: Int = 0,
    @SerialName("max_subid_length")val maxSubscriptionIdLength: Int = 0,
    @SerialName("max_event_tags")val maxEventTagNumber: Int = 0,
    @SerialName("max_content_length")val maxContentLength: Int = 0,
    @SerialName("min_pow_difficulty")val minPowDifficulty: Int = 0,
    @SerialName("auth_required")val isAuthRequired: Boolean = false,
    @SerialName("payment_required")val isPaymentRequired: Boolean = false,
    @SerialName("restricted_writes")val writesAreRestricted: Boolean = false,
    @SerialName("created_at_lower_limit")val creationDateLowerLimit: Long = 0L,
    @SerialName("created_at_upper_limit")val creationDateUpperLimit: Long = Long.MAX_VALUE
)

