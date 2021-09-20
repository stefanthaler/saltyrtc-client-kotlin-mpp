package net.thalerit.saltyrtc.core.util

import io.ktor.utils.io.core.*

/**
 * Convert unsigned integer to byte array
 * TODO remove dependency for ktor utilities
 */
fun UInt.toByteArray(): ByteArray {
    val builder = BytePacketBuilder()
    builder.writeUInt(this)
    return builder.build().readBytes(4)
}