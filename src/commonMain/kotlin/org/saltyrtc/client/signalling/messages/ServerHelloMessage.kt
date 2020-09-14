package org.saltyrtc.client.signalling.messages

import org.saltyrtc.client.crypto.NaClKey
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.SignallingMessage
import org.saltyrtc.client.signalling.SignallingMessageTypes

class ServerHelloMessage: IncomingSignallingMessage {
    override val TYPE: String = SignallingMessageTypes.SERVER_HELLO.type
    lateinit var key:NaClKey.NaClPublicKey

    constructor(nonce: Nonce, payloadMap: Map<String, Any>) : super(nonce, payloadMap) {
        //TODO validate all values
        key = NaClKey.NaClPublicKey(payloadMap.get("key") as ByteArray)
    }

    override fun validateSource(clientRole: SaltyRTCClient.Role) {
        if (nonce.source.toInt()!=0) {
            // A server SHALL use 0x00 as source address.
            throw ValidationError("ServerHelloMessage needs to originate from Server, was ${nonce.source}")
        }
    }
}