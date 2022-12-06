import fr.acinq.secp256k1.Hex
import ktnostr.crypto.CryptoUtils
import ktnostr.crypto.toHexString
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun main() {
    val kotlinStringHash = CryptoUtils.contentHash("Kotlin")
    println("SHA256 of Kotlin: ${kotlinStringHash.toHexString()}")
    if (kotlinStringHash.toHexString() == "c78f6c97923e81a2f04f09c5e87b69e085c1e47066a1136b5f590bfde696e2eb")
        println("Hashes match!") else println("Hashes do not match!")

    val currentTime = Instant.now()
    val currentTimestamp = currentTime.epochSecond
    println("Current timestamp: $currentTimestamp or ${System.currentTimeMillis().div(1_000L)}")
    println(
        "Formatted datetime: ${
            currentTime.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("uuuu MMM d hh:mm a"))
        }"
    )
    println(" ")
    testPubkeyGeneration()
}

fun testPubkeyGeneration(){
    val secKeyHex = "6ba903b7888191180a0959a6d286b9d0719d33a47395c519ba107470412d2069"
    val pubKeyHex = "8565b1a5a63ae21689b80eadd46f6493a3ed393494bb19d0854823a757d8f35f"
    val secKeyBytes = Hex.decode(secKeyHex)
    val actualPubKeyBytes = CryptoUtils.getPublicKey(secKeyBytes)
    val actualPubKeyHex = Hex.encode(actualPubKeyBytes)
    println("Correct pubkey: $pubKeyHex \n")
    println("Generated pubkey: $actualPubKeyHex")
}







