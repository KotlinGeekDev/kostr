
import fr.acinq.secp256k1.Hex
import ktnostr.crypto.CryptoUtils
import ktnostr.crypto.contentHash
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Suppress("NewApi")
fun main() {

val kotlinStringHash = CryptoUtils().contentHash("Kotlin")
println("SHA256 of Kotlin: ${kotlinStringHash.toHexString()}")
if(kotlinStringHash.toHexString() == "c78f6c97923e81a2f04f09c5e87b69e085c1e47066a1136b5f590bfde696e2eb")
println("Hashes match!") else println("Hashes do not match!")


val currentTime = Instant.now()
val currentTimestamp = currentTime.epochSecond
println("Current timestamp: $currentTimestamp or ${System.currentTimeMillis().div(1_000L)}")
println("Formatted datetime: ${currentTime.atZone(ZoneId.systemDefault())
    .format(DateTimeFormatter.ofPattern("uuuu MMM d hh:mm a"))}")
}


fun ByteArray.toHexString() = Hex.encode(this)

@Suppress("NewApi")
fun currentUnixTimeStampFromInstant(): Long  = Instant.now().epochSecond
fun currentSystemUnixTimeStamp(): Long = System.currentTimeMillis().div(1000L)

@Suppress("NewApi")
fun formattedDateTime(timestamp: Long): String {
    return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("uuuu MMM d hh:mm a"))
}