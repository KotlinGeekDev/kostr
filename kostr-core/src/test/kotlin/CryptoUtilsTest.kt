
import fr.acinq.secp256k1.Hex
import ktnostr.crypto.CryptoUtils
import org.junit.jupiter.api.Test


class CryptoUtilsTest {


    private val testContext = CryptoUtils()

    @Test
    fun `check that it correctly creates pubkeys`(){
        val testPublicKey = testContext.getPublicKey(Hex.decode(""))
        assert(Hex.encode(testPublicKey) == ""){
            "Pubkeys do not match!"
        }
    }
}