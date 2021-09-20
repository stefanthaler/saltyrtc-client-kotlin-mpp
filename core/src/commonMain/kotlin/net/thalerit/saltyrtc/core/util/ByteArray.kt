package net.thalerit.saltyrtc.core.util

//val byteToHexTable =initByteToHexTable ()


fun ByteArray.overrideSlice(startIndex: Int, bytes: ByteArray) {
    if (startIndex !in 0..this.size - bytes.size) throw IllegalArgumentException("startIndex must be a valid element within the bytearray[0-${this.size - bytes.size}], was ${startIndex}")
    for (i in startIndex..startIndex + bytes.size - 1) {
        this[i] = bytes[i - startIndex]
    }
}

// always
// TODO test
// TODO input validation
fun ByteArray.toUShort(): UShort {
    if (this.size != 2) throw IllegalArgumentException("only bytearrays with size 2 can be converted to ushort")
    return (((this[0].toUInt() and 0xFFu) shl 8) or (this[1].toUInt() and 0xFFu)).toUShort()
}
// little indian - need to reverse when big indian
// TODO test
// TODO input validation
//fun ByteArray.toUInt(): UInt {
//    if (this.size!=4) throw IllegalArgumentException("only bytearrays with size 4 can be converted to uint")
//    return (this[3].toUInt()*16777216u + this[2].toUInt()*65536u + this[1].toUInt()*256u + this[0].toUInt()).toUInt()
//}

fun ByteArray.toUInt(): UInt {
    if (this.size != 4) throw IllegalArgumentException("only bytearrays with size 4 can be converted to uint")
    return ((this[0].toUInt() and 0xFFu) shl 24) or
            ((this[1].toUInt() and 0xFFu) shl 16) or
            ((this[2].toUInt() and 0xFFu) shl 8) or
            (this[3].toUInt() and 0xFFu)
}


val hexChars = "0123456789ABCDEF".toCharArray()

fun byteToHex(b: Byte): String {
    var c = b.toUByte()
    return "${hexChars[(c / 16u).toInt()]}${hexChars[(c % 16u).toInt()]}"
}

fun ByteArray.toHexString(): String { //TODO test
    return this.joinToString("") { byteToHex(it) }
}


