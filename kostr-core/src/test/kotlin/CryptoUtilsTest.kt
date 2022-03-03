
import fr.acinq.secp256k1.Hex
import ktnostr.crypto.CryptoUtils
import ktnostr.crypto.toHexString
import org.junit.jupiter.api.Test


class CryptoUtilsTest {

//    @Test
//    fun `it needs to detect invalid seckeys when creating pubkeys`(){
//        val context = Secp256k1.get()
//
//        val error = assertThrows<IllegalArgumentException>("Invalid seckey"){
//            context.secKeyVerify(Hex.decode("6ba903b7888191180a0959a6d286b9d0719d33a47395c519ba107470412d2"))
//        }
//        assert("Failed requirement." == error.message)
//
//    }
    @Test
    fun `it needs to create correct pubkeys`(){
        val testContext = CryptoUtils.get()
        val testPublicKey = testContext.getPublicKey(Hex.decode("6ba903b7888191180a0959a6d286b9d0719d33a47395c519ba107470412d2069"))
        assert(Hex.encode(testPublicKey) == "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f"){
            "Pubkeys do not match!"
        }
    }

    @Test
    fun `the content hash generates correct hashes for a word`(){
        val context = CryptoUtils.get()
        val word = "Kotlin"
        val correctHash = "c78f6c97923e81a2f04f09c5e87b69e085c1e47066a1136b5f590bfde696e2eb"

        assert(context.contentHash(word).toHexString() == correctHash){
            "Hashes do not match!"
        }
    }


}