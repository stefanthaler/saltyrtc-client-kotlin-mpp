package org.saltyrtc.client.signalling.messages.incoming

import SaltyRTCClient
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.SignallingMessage
import org.saltyrtc.client.signalling.SignallingMessageTypes

class AuthMessage:IncomingSignallingMessage {
    override val TYPE: String = SignallingMessageTypes.KEY.type

    constructor(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce, client, payloadMap) {

    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }
}