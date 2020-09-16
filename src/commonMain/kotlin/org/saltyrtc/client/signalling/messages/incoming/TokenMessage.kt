package org.saltyrtc.client.signalling.messages.incoming

import SaltyRTCClient
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce

class TokenMessage(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : IncomingSignallingMessage(nonce, client,
    payloadMap) {
    override val TYPE: String = "token"

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }
}