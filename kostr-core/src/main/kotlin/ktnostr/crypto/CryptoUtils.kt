//@file:JvmName("CryptoUtils")

package ktnostr.crypto

import fr.acinq.secp256k1.Hex
import fr.acinq.secp256k1.Secp256k1
import fr.acinq.secp256k1.Secp256k1Exception
import java.security.MessageDigest
import java.security.SecureRandom
import javax.security.auth.Destroyable

//Class containing all the cryptographic helpers for Nostr
//TODO: Add signing and verification(to be tuned for events.)
class CryptoUtils internal constructor(): Destroyable {


    fun generatePrivateKey(): ByteArray {
        val context: Secp256k1 = Secp256k1.get()
        if (context == null) throw Secp256k1Exception("Secp256k1 provider failed to initialize.")
        val secretKey = ByteArray(32)
        val pseudoRandomBytes = SecureRandom()
        pseudoRandomBytes.nextBytes(secretKey)
        context.cleanup()
        return secretKey
    }

    fun getPublicKey(privateKey: ByteArray): ByteArray {
        val context: Secp256k1 = Secp256k1.get()
        if (privateKey == null) throw Error("There is no private key provided!")// just a check
        if (!context.secKeyVerify(privateKey)) throw Error("Invalid private key!")
        val pubKey = context.pubkeyCreate(privateKey).drop(1).take(32).toByteArray()
        context.cleanup()
        return pubKey
    }

    /**
     * Function that returns the hash of content
     * @param content the content to be hashed
     * @return the content hash, as a byte array.
     */
    //TODO: Should the function return a string or a byte array?
    fun contentHash(content: String): ByteArray {
        return MessageDigest.getInstance("SHA-256")
            .digest(content.toByteArray())
    }

    @Throws(Error::class)
    fun signContent(privateKey: ByteArray, content: String): ByteArray {
        if (privateKey == null) throw Error("Invalid private key!")
        val signingContext = Secp256k1.get()
        val contentInBytes = Hex.decode(content)
        val freshRandomBytes = ByteArray(32)
        SecureRandom().nextBytes(freshRandomBytes)
        val contentSignature = signingContext.signSchnorr(contentInBytes, privateKey, freshRandomBytes)
        return contentSignature
    }

    companion object {

        @JvmStatic
        fun get(): CryptoUtils {
            return CryptoUtils()
        }

    }



}


fun ByteArray.toHexString() = Hex.encode(this)