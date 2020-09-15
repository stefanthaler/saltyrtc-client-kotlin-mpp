package org.saltyrtc.client.signalling.messages

import SaltyRTCClient
import org.saltyrtc.client.crypto.NaClKey
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.SignallingMessage
import org.saltyrtc.client.signalling.SignallingMessageTypes

class ServerHelloMessage: IncomingSignallingMessage {
    override val TYPE: String = SignallingMessageTypes.SERVER_HELLO.type

    lateinit var key:NaClKey.NaClPublicKey

    constructor(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce,client, payloadMap) {
        //TODO validate all values
        key = NaClKey.NaClPublicKey(payloadMap.get("key") as ByteArray)
    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
        if (nonce.source.toInt()!=0) {
            // A server SHALL use 0x00 as source address.
            throw ValidationError("ServerHelloMessage needs to originate from Server, was ${nonce.source}")
        }
    }
}