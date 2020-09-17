package org.saltyrtc.client.extensions

val hexChars = "0123456789ABCDEF".toCharArray()
//val byteToHexTable =initByteToHexTable ()

fun byteToHex(b:Byte):String{
    var c = b.toUByte()
    return "${hexChars[(c/ 16u).toInt()]}${hexChars[(c% 16u).toInt()]}"
}

fun ByteArray.overrideSlice(startIndex: Int, bytes: ByteArray) {
    if (startIndex !in 0..this.size-1-bytes.size) throw IllegalArgumentException("startIndex must be a valid element within the bytearray[0-${this.size-1-bytes.size}], was ${startIndex}")
    for (i in startIndex .. startIndex+bytes.size-1) {
        this[i] = bytes[i-startIndex]
    }
}

// always
// TODO test
// TODO input validation
fun ByteArray.toUShort(): UShort {
    return ((this.get(1).toInt() shl 8).toUShort() + this.get(0).toUShort()).toUShort()
}
// little indian - need to reverse when big indian
// TODO test
// TODO input validation
fun ByteArray.toUInt(): UInt {
    return (this.get(3).toInt() shl 24).toUInt() + (this.get(2).toInt() shl 16).toUInt() + (this.get(1).toInt() shl 8).toUInt() + this.get(0).toUInt()
}

fun ByteArray.toHexString(): String { //TODO test
    return this.joinToString (""){ byteToHex(it) }
}


