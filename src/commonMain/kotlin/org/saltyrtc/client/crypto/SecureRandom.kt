package org.saltyrtc.client.crypto

/**
 * Securely random bytes
 */

expect fun secureRandomeBytes(numBytes:Int):ByteArray

/**
 * Next securely random Int
 */

expect fun secureRandomInt():UInt
