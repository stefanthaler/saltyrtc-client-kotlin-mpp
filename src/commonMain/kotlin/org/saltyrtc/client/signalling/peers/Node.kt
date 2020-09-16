package org.saltyrtc.client.signalling.peers

import org.saltyrtc.client.crypto.NaClKey
import org.saltyrtc.client.crypto.secureRandomInt
import org.saltyrtc.client.crypto.secureRandomeBytes
import org.saltyrtc.client.signalling.Cookie
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.states.State

abstract class Node {
    lateinit var state: State<out IncomingSignallingMessage>
    var yourCookie = Cookie()
    var theirCookie: Cookie? = null
    var nonce: Nonce? = null
    var identity:Byte = 0
    var publicKey:NaClKey.NaClPublicKey? = null

    fun nextNonce(destination:Byte):Nonce {
        if (nonce==null) {
            // A client SHALL also generate a new cryptographically secure random cookie to be used for the other peer. In case the other peer is a server, the cookie is valid until the connection has been closed. For other clients, this cookie is valid until the server announces a new initiator or responder with the same address or until the connection to the server has been closed.
            nonce = Nonce(yourCookie!!,identity, destination, 0u,  secureRandomInt() )

        } else {
            //In case this is not the first message to the destination peer, the peer does the following:

            //In case that the peer does make use of the combined sequence number, it MUST increase the combined sequence
            // number of the destination peer by 1 and check that is has not reset to 0. Implementations that use the
            // combined sequence number SHALL ignore the following two procedures.
            //Increment the sequence number of the destination peer by 1. In case that it overflows (and resets to 0),
            // the overflow number of the destination peer MUST be increased by 1 as well, and
            // in case the overflow number of the destination peer has been incremented by 1, it SHALL NOT reset to 0 if
            // it was greater than 0 before.

            if (nonce!!.sequenceNumber<UInt.MAX_VALUE) {
                nonce = Nonce(yourCookie!!,identity, destination, nonce!!.overflowNumber,  nonce!!.sequenceNumber+1u )
            } else {
                nonce = Nonce(yourCookie!!,identity, destination, (nonce!!.overflowNumber+1u).toUShort(),  0u )
            }
        }
        require(!yourCookie.bytes.contentEquals(theirCookie?.bytes))

        return nonce!!
    }
}