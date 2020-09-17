package org.saltyrtc.client.extensions

fun Byte.toByteArray(): ByteArray {
    return ByteArray(1).also { it[0] = this }
}

fun Byte.reverseBytes(): Byte {
    val v0 = ((this.toInt() ushr 0) and 0xFF)
    return (v0 shl 8).toByte()
}
