package ktnostr.crypto

import dev.whyoleg.cryptography.openssl3.Openssl3
import dev.whyoleg.cryptography.provider.CryptographyProvider
import dev.whyoleg.cryptography.random.CryptographyRandom

actual fun SecureRandom() : CryptographyRandom {
    return CryptographyRandom
}

actual fun getCryptoProvider(): CryptographyProvider {
    return CryptographyProvider.Openssl3
}