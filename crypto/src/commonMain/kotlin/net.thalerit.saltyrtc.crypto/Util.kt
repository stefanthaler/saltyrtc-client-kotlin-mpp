package net.thalerit.saltyrtc.crypto

private val hexChars = "0123456789ABCDEF".toCharArray()

fun byteToHex(b: Byte): String {
    var c = b.toUByte()
    return "${hexChars[(c / 16u).toInt()]}${hexChars[(c % 16u).toInt()]}"
}

fun ByteArray.toHexString(): String { //TODO test
    return this.joinToString("") { byteToHex(it) }
}

private val lowerDigit = mapOf( // maps chars to 16^0
    '0' to 0, '1' to 1, '2' to 2, '3' to 3, '4' to 4,
    '5' to 5, '6' to 6, '7' to 7, '8' to 8,
    '9' to 9, 'A' to 10, 'B' to 11, 'C' to 12,
    'D' to 13, 'E' to 14, 'F' to 15
)

private val upperDigit = lowerDigit.mapValues { it.value * 16 } // maps chars to 16^1

fun String.fromHexToByteArray(): ByteArray {
    if (this.isEmpty()) return ByteArray(0)
    if (this.length % 2 == 1) throw IllegalArgumentException("Hex String should always contain 2 characters for 2 byte. ")
    val out = ByteArray(this.length / 2)
    val chars = this.uppercase().toCharArray()

    // a byte consists of two hex chars, e.g. AF
    // byte value is int(A)*16^1 + int(F)*16^0
    for (i in 0 until this.length step 2) {
        if (chars[i] !in lowerDigit.keys || chars[i + 1] !in lowerDigit.keys) throw IllegalArgumentException("Illegal character in hex string: '$this' at position($i-${i + 1}). Allowed: 0-9, A-F")
        out[i / 2] = (upperDigit[chars[i]]!! + lowerDigit[chars[i + 1]]!!).toByte()
    }
    return out
}
