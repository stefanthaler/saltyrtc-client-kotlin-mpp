package org.saltyrtc.client.entity


import org.saltyrtc.client.Cookie
import org.saltyrtc.client.Nonce
import org.saltyrtc.client.crypto.secureRandomInt
import org.saltyrtc.client.state.Identity
import org.saltyrtc.client.state.ServerIdentity
import org.saltyrtc.client.util.*
import kotlin.jvm.JvmInline

const val NONCE_LENGTH = 24

@JvmInline
value class OverflowNumber(val number: UShort)

@JvmInline
value class SequenceNumber(val number: UInt)

fun nonce(frame: ByteArray): Nonce {
    val cookie = cookie(frame.sliceArray(0..15))
    val source = Identity(frame[16])
    val destination = Identity(frame[17])
    val overflowNumber = OverflowNumber(frame.sliceArray(18..19).toUShort())
    val sequenceNumber = SequenceNumber(frame.sliceArray(20..23).toUInt())
    return NonceImpl(cookie, source, destination, overflowNumber, sequenceNumber)
}

fun nonce(): Nonce = nonce(ServerIdentity, ServerIdentity)

fun nonce(
    source: Identity,
    destination: Identity,
): Nonce {
    return NonceImpl(
        cookie = cookie(),
        source = source,
        destination = destination,
        overflowNumber = OverflowNumber(0.toUShort()),
        sequenceNumber = SequenceNumber(secureRandomInt()),
    )
}

fun nonce(cookie: Cookie): Nonce {
    return NonceImpl(
        cookie = cookie,
        source = ServerIdentity,
        destination = ServerIdentity,
        overflowNumber = OverflowNumber(0.toUShort()),
        sequenceNumber = SequenceNumber(secureRandomInt()),
    )
}

data class NonceImpl(
    override val cookie: Cookie,
    override val source: Identity,
    override val destination: Identity,
    override val overflowNumber: OverflowNumber,
    override val sequenceNumber: SequenceNumber,
) : Nonce {

    override val bytes: ByteArray by lazy {
        val frame = ByteArray(NONCE_LENGTH)
        frame.overrideSlice(0, cookie.bytes)
        frame[16] = source.address
        frame[17] = destination.address
        frame.overrideSlice(18, overflowNumber.number.toByteArary())
        frame.overrideSlice(20, sequenceNumber.number.toByteArray())
        frame
    }
}

fun Nonce.withIncreasedSequenceNumber(): Nonce {
    this as NonceImpl
    return if (sequenceNumber.number < UInt.MAX_VALUE) {
        copy(
            sequenceNumber = SequenceNumber(sequenceNumber.number + 1u)
        )
    } else {
        copy(
            sequenceNumber = SequenceNumber(0u),
            overflowNumber = OverflowNumber((overflowNumber.number + 1u).toUShort())
        )
    }
}

fun Nonce.withSource(source: Identity): Nonce {
    this as NonceImpl
    return copy(
        source = source
    )
}

fun Nonce.withDestination(destination: Identity): Nonce {
    this as NonceImpl
    return copy(
        destination = destination
    )
}