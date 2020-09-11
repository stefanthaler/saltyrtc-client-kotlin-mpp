package org.saltyrtc.client.extensions

import io.ktor.utils.io.core.*

/**
 * Convert unsigned short to byte array
 * TODO test
 */
fun UShort.toByteArary(): ByteArray {
    val builder = BytePacketBuilder()
    builder.writeUShort(this)
    return builder.build().readBytes(2)
}

