package net.thalerit.saltyrtc.tasks.message

import net.thalerit.saltyrtc.api.Cookie
import net.thalerit.saltyrtc.api.NONCE_LENGTH
import net.thalerit.saltyrtc.api.OverflowNumber
import net.thalerit.saltyrtc.api.SequenceNumber
import net.thalerit.saltyrtc.core.entity.cookie
import net.thalerit.saltyrtc.core.util.overrideSlice
import net.thalerit.saltyrtc.core.util.toByteArray
import net.thalerit.saltyrtc.core.util.toUInt
import net.thalerit.saltyrtc.core.util.toUShort


internal fun sdcNonce(frame: ByteArray): SdcNonce {
    val cookie = cookie(frame.sliceArray(0..15))
    val dataChannelId = DataChannelId(frame.sliceArray(16..17).toUShort())
    val overflowNumber = OverflowNumber(frame.sliceArray(18..19).toUShort())
    val sequenceNumber = SequenceNumber(frame.sliceArray(20..23).toUInt())
    return SdcNonce(cookie, dataChannelId, overflowNumber, sequenceNumber)
}

internal data class SdcNonce(
    val cookie: Cookie,
    val dataChannelId: DataChannelId,
    val overflowNumber: OverflowNumber,
    val sequenceNumber: SequenceNumber,
) {
    val bytes: ByteArray by lazy {
        val frame = ByteArray(NONCE_LENGTH)
        frame.overrideSlice(0, cookie.bytes)
        frame.overrideSlice(16, dataChannelId.id.toByteArray())
        frame.overrideSlice(18, overflowNumber.number.toByteArray())
        frame.overrideSlice(20, sequenceNumber.number.toByteArray())
        frame
    }
}