package org.saltyrtc.client.signalling.messages.incoming.server

import SaltyRTCClient
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce

class NewInitiatorMessage: IncomingSignallingMessage {
    override val type  = Type( "new-initiator")

    constructor(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce,client, payloadMap) {
        //TODO validate reason number
    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }
}