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

import kotlinx.coroutines.test.runTest
import rhodium.nostr.relay.info.PaymentInfo
import rhodium.nostr.relay.info.Payments
import rhodium.nostr.relay.info.RelayLimits
import kotlin.test.Test
import kotlin.test.assertEquals

class RelayInfoTests {


    @Test
    fun generatedAndManualRelayInfoAreTheSame() = runTest {
        val edenNostrLandInfo = Relay.Info(
            description = "[✨ NFDB] nostr.land family of relays (fi-01 [tiger])",
            name = "[✨ NFDB] nostr.land",
            icon = "https://i.nostr.build/b3thno790aodH8lE.jpg",
            pubkey = "52b4a076bcbbbdc3a1aefa3735816cf74993b1b8db202b01c883c58be7fad8bd",
            relaySoftware = "NFDB",
            softwareVersion = "1.0.0",
            termsOfService = "https://nostr.land/terms",
            supportedNips = intArrayOf(1, 2, 4, 8, 9, 10, 11, 13, 14, 15, 16, 17, 18, 19, 21, 22, 23, 24, 25, 27, 28, 30, 31, 32, 34, 35, 36, 37, 38, 39, 40, 42, 44, 46, 47, 48, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 64, 65, 68, 69, 71, 72, 73, 75, 78, 84, 88, 89, 90, 92, 99),
            limits = RelayLimits(
                maxMessageLength = 65535,
                maxEventTagNumber = 2000,
                maxSubscriptions = 200,
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