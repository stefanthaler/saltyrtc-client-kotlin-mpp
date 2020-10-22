package org.saltyrtc.client.signalling.messages.incoming.client.handshake

import SaltyRTCClient
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.messages.SignallingMessageTypes

class KeyMessage: TokenOrKeyMessage {
    override val TYPE = Type(SignallingMessageTypes.KEY.type)

    constructor(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce, client, payloadMap) {

    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }
}