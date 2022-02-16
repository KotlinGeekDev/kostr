//@file:JvmName("CryptoUtils")

package ktnostr.crypto

import fr.acinq.secp256k1.Secp256k1
import fr.acinq.secp256k1.Secp256k1Exception
import java.security.MessageDigest
import java.security.SecureRandom
import javax.security.auth.Destroyable

//Class containing all the cryptographic helpers for Nostr
//TODO: Add signing and verification(to be tuned for events.)
class CryptoUtils internal constructor(): Destroyable{

    private val context: Secp256k1 = Secp256k1.get()

    companion object {

        @JvmStatic
        fun get(): CryptoUtils = CryptoUtils()

    }



    fun generatePrivateKey(): ByteArray {

        if (context == null) throw Secp256k1Exception("Secp256k1 provider failed to initialize.")
        val secretKey = ByteArray(32)
        val pseudoRandomBytes = SecureRandom()
        pseudoRandomBytes.nextBytes(secretKey)

        return secretKey
    }

    fun getPublicKey(privateKey: ByteArray): ByteArray {
        if (privateKey == null) throw Error("There is no private key provided!")// just a check
        return context.pubkeyCreate(privateKey).drop(1).take(32).toByteArray()
    }

}

// Function that returns the hash of content
//TODO: Should the function return a string or a byte array?
fun CryptoUtils.contentHash(content: String): ByteArray {
    return MessageDigest.getInstance("SHA-256")
        .digest(content.toByteArray())
}