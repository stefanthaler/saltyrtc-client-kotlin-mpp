package org.saltyrtc.client.entity


import org.saltyrtc.client.Cookie
import org.saltyrtc.client.Nonce
import org.saltyrtc.client.crypto.secureRandomInt
import org.saltyrtc.client.extensions.*

const val NONCE_LENGTH = 24

fun Nonce(frame: ByteArray): Nonce {
    val cookie = Cookie(frame.sliceArray(0..15))
    val source = frame[16]
    val destination = frame[17]
    val overflowNumber: UShort = frame.sliceArray(18..19).toUShort()
    val sequenceNumber: UInt = frame.sliceArray(20..23).toUInt()
    return NonceImpl(cookie, source, destination, overflowNumber, sequenceNumber)
}

data class NonceImpl(
    override val cookie: Cookie = Cookie(),
    override val source: Byte = 0,
    override val destination: Byte = 0,
    override val overflowNumber: UShort = 0u,
    override val sequenceNumber: UInt = secureRandomInt()
) : Nonce {

    override val bytes: ByteArray by lazy {
        val frame = ByteArray(NONCE_LENGTH)
        frame.overrideSlice(0, cookie.bytes)
        frame[16] = source
        frame[17] = destination
        frame.overrideSlice(18, overflowNumber.toByteArary())
        frame.overrideSlice(20, sequenceNumber.toByteArray())
        frame
    }
}

fun Nonce.withIncreasedSequenceNumber(): Nonce {
    this as NonceImpl
    return if (sequenceNumber < UInt.MAX_VALUE) {
        copy(
            sequenceNumber = sequenceNumber + 1u
        )
    } else {
        copy(
            sequenceNumber = 0u,
            overflowNumber = (overflowNumber + 1u).toUShort()
        )
    }
}