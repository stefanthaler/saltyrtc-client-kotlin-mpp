package org.saltyrtc.client.signalling.messages

import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.SignallingMessage

class ServerHelloMessage: SignallingMessage {
    lateinit var key:ByteArray

    constructor(type: String, nonce: Nonce, payloadMap: Map<String, Any>) {
        //TODO validate all values

        key = payloadMap.get("key") as ByteArray
        if (key.size!=32) {
            throw ValidationError("Session public key must be exactly 32 bytes long, was ${key}")
        }
    }
}