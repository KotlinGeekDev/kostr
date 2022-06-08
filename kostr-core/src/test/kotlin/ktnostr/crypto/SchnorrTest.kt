package ktnostr.crypto

import fr.acinq.secp256k1.Hex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import java.util.*

class SchnorrSigTest {
    data class TV(
        val id: Int,
        val secKey: ByteArray?,
        val pubKey: ByteArray,
        val msg: ByteArray,
        val sig: ByteArray,
        val result: Boolean,
        val comment: String?
    )

    @ParameterizedTest
    @CsvFileSource(resources = ["/test_schnorr_data_with_priv_key.txt"], numLinesToSkip = 0)
    fun testGetPublicKey(id: String, secKeyHex: String, pubKeyHex: String, msgHex: String, sigHex: String, resultString: String, comment: String?) {
        val pubKeyHexActual = CryptoUtils.getPublicKey(Hex.decode(secKeyHex)).toHexString()
        assertEquals(pubKeyHex, pubKeyHexActual, "Pub Key wrong $id ($comment)")
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/test_schnorr_data_with_priv_key.txt"], numLinesToSkip = 0)
    fun testSignContent(id: String, secKeyHex: String, pubKeyHex: String, msgHex: String, sigHex: String, resultString: String, comment: String?) {
        val sigActual = CryptoUtils.signContent(Hex.decode(secKeyHex), Hex.decode(msgHex))
        assertEquals(sigHex, sigActual.toHexString(), "Signature wrong $id ($comment)")
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/test_schnorr_data_with_priv_key.txt", "/test_schnorr_data_no_priv_key.txt"], numLinesToSkip = 0)
    fun testAllVectors(id: String, secKeyHex: String?, pubKeyHex: String, msgHex: String, sigHex: String, resultString: String, comment: String?) {
        val resultActual = CryptoUtils.verifyContentSignature(Hex.decode(sigHex), Hex.decode(pubKeyHex.substring(2)), Hex.decode(msgHex))
        assertEquals(resultString == """"TRUE"""", resultActual, "Failed verification test $id ($comment)")
    }
}

class SchnorrKeysTest {
    @Test
    fun testKeyPair() {
        val secKey = "dca4f4bf2883e4502200d7831ad891ace8c895709e9f09c9f9692632ae36c482".uppercase(Locale.US)
        val pubKey = "ce16d1d2fabca7184d1502c147d5e029e88e63f8ff31ebfe3dbc9677819061cf".uppercase(Locale.US)
        testKeys(secKey, pubKey)
    }

    @Test
    fun testSecKey() {
        repeat(100) {
            val secKey = CryptoUtils.generatePrivateKey()
            val secKeyHex = Hex.encode(secKey)
            val pubKey = CryptoUtils.getPublicKey(secKey)
            val pubKeyHex = Hex.encode(pubKey)
            testKeys(secKeyHex, pubKeyHex)
        }
    }

    private fun testKeys(secKeyHex: String, pubKeyHex: String) {
        val secKeyBytes = Hex.decode(secKeyHex)
        val pubKeyResult = CryptoUtils.getPublicKey(secKeyBytes)
        val pubKeyResultHex = Hex.encode(pubKeyResult)
        assertEquals(pubKeyHex, pubKeyResultHex)
    }
}
