package org.saltyrtc.client.signalling


import org.saltyrtc.client.crypto.secureRandomInt
import org.saltyrtc.client.extensions.*
import org.saltyrtc.client.logging.logWarn

class Nonce(val cookie:Cookie= Cookie(), var source: Byte=0, var destination: Byte=0, var overflowNumber: UShort=0u, var sequenceNumber: UInt= secureRandomInt()) {

    fun increaseSequenceNumber() {
        if (sequenceNumber<UInt.MAX_VALUE) {
            sequenceNumber = sequenceNumber+1u
        } else {
            overflowNumber= (overflowNumber+1u).toUShort()
            sequenceNumber=0u
        }
    }

    fun toByteArray():ByteArray {
        val frame = ByteArray(LENGTH)
        frame.overrideSlice(0, cookie.bytes)
        frame[16]=source
        frame[17]=destination
        frame.overrideSlice(18, overflowNumber.toByteArary() )
        frame.overrideSlice(20, sequenceNumber.toByteArray() )
        return frame
    }

    companion object{
        val LENGTH: Int = 24

        fun from(frame:ByteArray):Nonce {
            val cookie = Cookie(frame.sliceArray(0..15))
            val source = frame[16]
            val destination = frame[17]
            val overflowNumber:UShort = frame.sliceArray(18..19).toUShort()
            val sequenceNumber:UInt = frame.sliceArray(20..23).toUInt()
            logWarn("$overflowNumber")
            logWarn("HERE ${sequenceNumber.toByteArray().size}")

            return Nonce(cookie, source,  destination, overflowNumber, sequenceNumber)
        }
    }
}




