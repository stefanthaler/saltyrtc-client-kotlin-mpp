package org.saltyrtc.client.signalling.messages

import SaltyRTCClient
import org.saltyrtc.client.crypto.naclEncrypt
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.OutgoingSignallingMessage
import org.saltyrtc.client.signalling.SignallingMessage
import org.saltyrtc.client.signalling.SignallingMessageFields
import org.saltyrtc.client.signalling.SignallingMessageFields.*

/**
 * IncomingSignallingMessage
 * {
    "type": "client-auth",
    "your_cookie": b"af354da383bba00507fa8f289a20308a",
    "subprotocols": [
    "v1.saltyrtc.org",
    "some.other.protocol"
    ],
    "ping_interval": 30,
    "your_key": b"2659296ce03993e876d5f2abcaa6d19f92295ff119ee5cb327498d2620efc979"
    }

 */

class ClientAuthMessage: OutgoingSignallingMessage {
    override val TYPE: String = org.saltyrtc.client.signalling.SignallingMessageTypes.CLIENT_AUTH.type

    constructor(nonce: Nonce, client:SaltyRTCClient, pingInterval:Int=0) : super(nonce,client) {
        payloadMap["${SignallingMessageFields.TYPE}"] = TYPE

        // The client MUST set the your_cookie field to the cookie the server has used in the nonce of the 'server-hello' message.
        if (client.your_cookie==null) {
            throw ValidationError("Cookie should be set in ClientAuthMessage, was null")
        }
        payloadMap["${YOUR_COOKIE}"] = client.your_cookie!!

        if (client.signallingServer==null) {
            throw ValidationError("Signalling server has to be set in ClientAuthMessage")
        }
        //It SHALL also set the subprotocols field to the exact same Array of subprotocol strings it has provided to the WebSocket client implementation for subprotocol negotiation.
        payloadMap["${SUBPROTOCOLS}"] = arrayOf(client.signallingServer!!.subProtocol)

        //If the user application requests to be pinged (see RFC 6455 section 5.5.2) in a specific interval, the client SHALL set the field ping_interval to the requested interval in seconds. Otherwise, ping_interval MUST be set to 0 indicating that no WebSocket ping messages SHOULD be sent.
        if (pingInterval<0) {
            throw ValidationError("Ping interval should not be set to a negative number, was $pingInterval")
        }
        payloadMap["${PING_INTERVAL}"] = pingInterval

        // If the client has stored the server's public permanent key (32 bytes), it SHOULD set it in the your_key field.
        if (client.sessionPublicKey!=null) {
            payloadMap["$YOUR_KEY"] = client.sessionPublicKey!!
        }
    }

    /**
     * The message SHALL be NaCl public-key encrypted:
     *  by the server's session key pair (public key sent in 'server-hello')
     *  the client's permanent key pair (public key as part of the WebSocket path or sent in 'client-hello').
     */
    override fun encrypt(payloadBytes: ByteArray): ByteArray {
        var encPayloadBytes = naclEncrypt(client.clientPermanentKey.privateKey, payloadBytes)
        if (client.sessionPublicKey==null) {
            throw ValidationError("Session public key was null, should be set after server hello message")
        }
        return naclEncrypt(client.sessionPublicKey!!,encPayloadBytes)
    }


}