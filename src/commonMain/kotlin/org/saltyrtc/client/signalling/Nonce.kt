package org.saltyrtc.client.signalling

import org.saltyrtc.client.extensions.toUInt
import org.saltyrtc.client.extensions.toUShort

class Nonce(val cookie:Cookie,val source: Byte,  val destination: Byte, val overflowNumber: UShort, val sequenceNumber: UInt ) {

    fun toByteArray():ByteArray {
        //TODO continue here
        return cookie.bytes
    }

    companion object{
        fun from(frame:ByteArray):Nonce {
            val cookie = Cookie(frame.sliceArray(0..15))
            val source = frame[16]
            val destination = frame[17]
            val overflowNumber:UShort = frame.sliceArray(18..19).toUShort()
            val sequenceNumber:UInt =  frame.sliceArray(20..23).toUInt()
            return Nonce(cookie, source,  destination, overflowNumber, sequenceNumber,)
        }
    }
}


