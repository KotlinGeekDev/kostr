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
data class RetentionPolicy(
    @SerialName("kinds") val retainedKinds: IntArray = emptyArray<Int>().toIntArray(),
    @SerialName("time") val retentionTime: Long? = 0L,
    @SerialName("count") val retainedEventCount: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RetentionPolicy

        if (retentionTime != other.retentionTime) return false
        if (retainedEventCount != other.retainedEventCount) return false
        if (!retainedKinds.contentEquals(other.retainedKinds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = retentionTime?.hashCode() ?: 0
        result = 31 * result + retainedEventCount
        result = 31 * result + retainedKinds.contentHashCode()
        return result
    }
}