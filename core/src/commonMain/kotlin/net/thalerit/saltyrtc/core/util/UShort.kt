package net.thalerit.saltyrtc.core.util

import io.ktor.utils.io.core.*

/**
 * Convert unsigned short to byte array
 * TODO remove dependency for ktor utilities
 */
fun UShort.toByteArray(): ByteArray {
    val builder = BytePacketBuilder()
    builder.writeUShort(this)
    return builder.build().readBytes(2)
}


