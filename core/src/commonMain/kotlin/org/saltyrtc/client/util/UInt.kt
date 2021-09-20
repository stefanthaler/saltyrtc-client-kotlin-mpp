package org.saltyrtc.client.util

import io.ktor.utils.io.core.*

/**
 * Convert unsigned integer to byte array
 * TODO test
 */
fun UInt.toByteArray(): ByteArray {
    val builder = BytePacketBuilder()
    builder.writeUInt(this)
    return builder.build().readBytes(4)
}