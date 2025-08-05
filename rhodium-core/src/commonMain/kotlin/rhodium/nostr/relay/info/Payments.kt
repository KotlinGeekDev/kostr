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
 * Represents the different payment options given by the relay, provided the relay has paid features.
 * For more, see [Pay-to-Relay](https://github.com/nostr-protocol/nips/blob/master/11.md#pay-to-relay).
 * The payment options are divided into 3 nominal categories: admission, subscription, and publication.
 * Each category has a set of payment choices, each choice represented by a [PaymentInfo] object.
 *
 *@see PaymentInfo
 *
 * @property admissionFees The payment choices for *admission*(admission tiers), as an array.
 * @property subscriptionFees The payment choices for *subscription*(subscription tiers), as an array.
 * @property publicationFees The payment choices for *event publication*(publishing tiers), as an array.
 */
@Serializable
data class Payments(
    @SerialName("admission") val admissionFees: Array<PaymentInfo>? = null,
    @SerialName("subscription") val subscriptionFees: Array<PaymentInfo>? = null,
    @SerialName("publication") val publicationFees: Array<PaymentInfo>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Payments

        if (!admissionFees.contentEquals(other.admissionFees)) return false
        if (!subscriptionFees.contentEquals(other.subscriptionFees)) return false
        if (!publicationFees.contentEquals(other.publicationFees)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = admissionFees?.contentHashCode() ?: 0
        result = 31 * result + (subscriptionFees?.contentHashCode() ?: 0)
        result = 31 * result + (publicationFees?.contentHashCode() ?: 0)
        return result
    }
}

/**
 * Represents a payment choice, or tier, provided by the relay.
 *
 * @see Payments
 *
 * @property amount The amount to be paid for this tier.
 * @property unit The currency(or units of currency) for the amount.
 * @property durationInSeconds Specifies how long the usage of this tier will last.
 * @property eventKinds - The event kinds that will be retained,
 * or allowed for publication if paying for this tier.
 */
@Serializable
data class PaymentInfo(
    val amount: Long,
    val unit: String,
    @SerialName("period") val durationInSeconds: Long? = null,
    @SerialName("kinds") val eventKinds: IntArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PaymentInfo

        if (amount != other.amount) return false
        if (durationInSeconds != other.durationInSeconds) return false
        if (unit != other.unit) return false
        if (!eventKinds.contentEquals(other.eventKinds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = amount.hashCode()
        result = 31 * result + (durationInSeconds?.hashCode() ?: 0)
        result = 31 * result + unit.hashCode()
        result = 31 * result + (eventKinds?.contentHashCode() ?: 0)
        return result
    }
}