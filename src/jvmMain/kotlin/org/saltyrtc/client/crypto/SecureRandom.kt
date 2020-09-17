package org.saltyrtc.client.crypto

import java.security.SecureRandom

val random = SecureRandom();

/**
 * Securely random bytes
 */
actual fun secureRandomeBytes(numBytes: Int): ByteArray {
    val randomBytes = ByteArray(numBytes)
    random.nextBytes(randomBytes)
    return randomBytes
}

/**
 * Next securely random Int
 */
actual fun secureRandomInt(): UInt {
    return random.nextInt().toUInt()
}