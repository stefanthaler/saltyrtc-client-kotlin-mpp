package org.saltyrtc.client.extensions

fun Byte.toByteArray(): ByteArray {
    return ByteArray(1).also { it[0] = this }
}
