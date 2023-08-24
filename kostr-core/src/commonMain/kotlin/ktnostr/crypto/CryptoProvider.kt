package ktnostr.crypto

import dev.whyoleg.cryptography.provider.CryptographyProvider
import dev.whyoleg.cryptography.random.CryptographyRandom

expect fun SecureRandom(): CryptographyRandom
expect fun getCryptoProvider(): CryptographyProvider