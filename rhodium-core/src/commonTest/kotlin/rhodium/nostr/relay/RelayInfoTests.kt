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
import io.ktor.http.URLBuilder
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import rhodium.nostr.relay.Relay
import rhodium.nostr.relay.info.PaymentInfo
import rhodium.nostr.relay.info.Payments
import rhodium.nostr.relay.info.RelayLimits
import kotlin.test.Test
import kotlin.test.assertEquals

class RelayInfoTests {


    @Test
    fun generatedAndManualRelayInfoAreTheSame() = runTest {
        val edenNostrLandInfo = Relay.Info(
            description = "nostr.land family of relays (us-or-01)",
            name = "nostr.land",
            pubkey = "52b4a076bcbbbdc3a1aefa3735816cf74993b1b8db202b01c883c58be7fad8bd",
            relaySoftware = "custom",
            softwareVersion = "1.0.1",
            supportedNips = intArrayOf(1,2,4,9,11,12,16,20,22,28,33,40),
            limits = RelayLimits(
                maxMessageLength = 65535,
                maxEventTagNumber = 2000,
                maxSubscriptions = 20,
                isAuthRequired = false,
                isPaymentRequired = true,
            ),
            paymentUrl = "https://nostr.land",
            paymentInfo = Payments(
                subscriptionFees = arrayOf(
                    PaymentInfo(amount = 4000000, unit = "msats", durationInSeconds = 2592000)
                )
            )
        )

        val obtainedAndParsedInfo = Relay.fetchInfoFor("wss://eden.nostr.land")
        println("Obtained relay info: ")
        println(obtainedAndParsedInfo)

        assertEquals(edenNostrLandInfo, obtainedAndParsedInfo)
    }

}