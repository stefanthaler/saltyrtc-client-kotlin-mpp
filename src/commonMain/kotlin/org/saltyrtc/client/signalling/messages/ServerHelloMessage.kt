package org.saltyrtc.client.signalling.messages

import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.SignallingMessage
import org.saltyrtc.client.signalling.SignallingMessageTypes

class ServerHelloMessage: IncomingSignallingMessage {
    override val TYPE: String = SignallingMessageTypes.SERVER_HELLO.type
    lateinit var key:ByteArray

    constructor(nonce: Nonce, payloadMap: Map<String, Any>) : super(nonce, payloadMap) {
        //TODO validate all values

        key = payloadMap.get("key") as ByteArray
        if (key.size!=32) {
            throw ValidationError("Session public key must be exactly 32 bytes long, was ${key}")
        }
    }

    override fun validateSource(clientRole: SaltyRTCClient.Role) {
        if (nonce.source.toInt()!=0) {
            // A server SHALL use 0x00 as source address.
            throw ValidationError("ServerHelloMessage needs to originate from Server, was ${nonce.source}")
        }
    }


}