package net.thalerit.saltyrtc.crypto

import java.security.SecureRandom

private val random = SecureRandom()

actual fun secureRandomBytes(numBytes: Int): ByteArray {
    val randomBytes = ByteArray(numBytes)
    random.nextBytes(randomBytes)
    return randomBytes
}

actual fun secureRandomInt(): UInt {
    return random.nextInt().toUInt()
}