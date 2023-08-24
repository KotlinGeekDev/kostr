package ktnostr.crypto

import dev.whyoleg.cryptography.jdk.JDK
import dev.whyoleg.cryptography.provider.CryptographyProvider
import dev.whyoleg.cryptography.random.CryptographyRandom
import dev.whyoleg.cryptography.random.asCryptographyRandom
import java.security.SecureRandom

actual fun SecureRandom() : CryptographyRandom = SecureRandom().asCryptographyRandom()

actual fun getCryptoProvider(): CryptographyProvider {
    return CryptographyProvider.JDK
}