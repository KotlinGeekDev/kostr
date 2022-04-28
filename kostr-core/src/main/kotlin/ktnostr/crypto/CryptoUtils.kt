//@file:JvmName("CryptoUtils")

package ktnostr.crypto

import fr.acinq.secp256k1.Hex
import fr.acinq.secp256k1.Secp256k1
import fr.acinq.secp256k1.Secp256k1Exception
import java.security.MessageDigest
import java.security.SecureRandom
import javax.security.auth.Destroyable

//Class containing all the cryptographic helpers for Nostr
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

    /**
     * Generates(creates) a 32-byte public key from the provided private key.
     * @param privateKey the 32-byte private key, provided as a byte
     * array.
     *
     * @return the public key, as a byte array.
     */
    fun getPublicKey(privateKey: ByteArray): ByteArray {
        if (privateKey == null) throw Exception("There is no private key provided!")// just a check
        val context: Secp256k1 = Secp256k1.get()
        if (!context.secKeyVerify(privateKey)) throw Exception("Invalid private key!")
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

    /**
     * The function signs the content provided to it, and
     * returns the 64-byte schnorr signature of the content.
     * @param privateKey the private key used for signing, provided
     * as a byte array.
     * @param content the content to be signed, provided as a
     * byte array.
     *
     * @return the 64-byte signature, as a byte array.
     */
    @Throws(Error::class)
    fun signContent(privateKey: ByteArray, content: ByteArray): ByteArray {
        if (privateKey == null) throw Error("There is no private key provided!")
        val signingContext = Secp256k1.get()
        val freshRandomBytes = ByteArray(32)
        SecureRandom().nextBytes(freshRandomBytes)
        val contentSignature = signingContext.signSchnorr(content, privateKey, freshRandomBytes)
        signingContext.cleanup()
        return contentSignature
    }

    /**
     * The function verifies the provided 64-byte signature.
     * @param signature the signature to provide, as a byte array.
     * @param publicKey the 32-byte public key to provide, as a byte array.
     * @param content the signed content to provide, as a byte array.
     *
     * @return the validity of the signature, as a boolean.
     */
    @Throws(Secp256k1Exception::class)
    fun verifyContentSignature(signature: ByteArray, publicKey: ByteArray, content: ByteArray): Boolean {
        if (signature ==null) throw Exception("No signature provided!")
        if (publicKey == null) throw Exception("No public key/invalid public key provided!")
        if (content == null) throw Exception("No content/invalid content provided!")
        val verificationContext = Secp256k1.get()
        val verificationStatus = verificationContext.verifySchnorr(signature, content, publicKey)
        verificationContext.cleanup()
        return verificationStatus

    }

    companion object {

        @JvmStatic
        fun get(): CryptoUtils {
            return CryptoUtils()
        }

    }



}


fun ByteArray.toHexString() = Hex.encode(this)