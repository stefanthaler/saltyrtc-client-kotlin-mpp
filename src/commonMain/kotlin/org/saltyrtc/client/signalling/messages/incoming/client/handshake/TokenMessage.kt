package org.saltyrtc.client.signalling.messages.incoming.client.handshake

import SaltyRTCClient
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.messages.SignallingMessageTypes

abstract class TokenOrKeyMessage(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : IncomingSignallingMessage(nonce, client,
    payloadMap) {
}

class TokenMessage: TokenOrKeyMessage {
    override val type = Type (SignallingMessageTypes.TOKEN.type)

    constructor(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce, client, payloadMap) {

    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }
}