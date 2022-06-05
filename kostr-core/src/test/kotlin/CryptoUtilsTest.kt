import fr.acinq.secp256k1.Hex
import fr.acinq.secp256k1.Secp256k1
import ktnostr.crypto.CryptoUtils
import ktnostr.crypto.toHexString
import org.junit.Test
import kotlin.test.assertEquals


class CryptoUtilsTest {
    @Test
    fun testGetPublicKey() {
        val secKeyHex = "6ba903b7888191180a0959a6d286b9d0719d33a47395c519ba107470412d2069"
        val pubKeyHex = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f"
        val secKeyBytes = Hex.decode(secKeyHex)
        val pubKeyBytes = CryptoUtils.getPublicKey(secKeyBytes)
        val pubKeyHexActual = Hex.encode(pubKeyBytes)
        assertEquals(pubKeyHex, pubKeyHexActual, "PubKeys do not match!")
    }

    @Test
    fun testContentHash() {
        val content = "Kotlin"
        val hashHex = "c78f6c97923e81a2f04f09c5e87b69e085c1e47066a1136b5f590bfde696e2eb"
        val hashActualHex = CryptoUtils.contentHash(content).toHexString()
        assertEquals(hashHex, hashActualHex, "Hashes do not match!")
    }
}