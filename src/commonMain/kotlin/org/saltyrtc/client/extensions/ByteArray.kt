package org.saltyrtc.client.extensions

val hexChars = "0123456789ABCDEF".toCharArray()
//val byteToHexTable =initByteToHexTable ()

fun byteToHex(b:Byte):String{
    var c = b.toUByte()
    return "${hexChars[(c/ 16u).toInt()]}${hexChars[(c% 16u).toInt()]}"
}

fun Short.reverseBytes(): Short {
    val v0 = ((this.toInt() ushr 0) and 0xFF)
    val v1 = ((this.toInt() ushr 8) and 0xFF)
    return ((v1 and 0xFF) or (v0 shl 8)).toShort()
}

// always
// TODO test
fun ByteArray.toUShort(): UShort {
    return ((this.get(1).toInt() shl 8).toUShort() + this.get(0).toUShort()).toUShort()
}
// little indian - need to reverse when big indian
// TODO test
fun ByteArray.toUInt(): UInt {
    return (this.get(3).toInt() shl 24).toUInt() + (this.get(2).toInt() shl 16).toUInt() + (this.get(1).toInt() shl 8).toUInt() + this.get(0).toUInt()
}

fun ByteArray.toHexString(): String { //TODO test
    return this.joinToString (""){ byteToHex(it) }
}


