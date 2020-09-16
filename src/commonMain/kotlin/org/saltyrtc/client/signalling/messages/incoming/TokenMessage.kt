package org.saltyrtc.client.signalling.messages.incoming

import SaltyRTCClient
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.SignallingMessageFields
import org.saltyrtc.client.signalling.SignallingMessageTypes

abstract class TokenOrKeyMessage(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : IncomingSignallingMessage(nonce, client,
    payloadMap) {
}

class TokenMessage:TokenOrKeyMessage {
    override val TYPE: String = SignallingMessageTypes.TOKEN.type

    constructor(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce, client, payloadMap) {

    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }
}