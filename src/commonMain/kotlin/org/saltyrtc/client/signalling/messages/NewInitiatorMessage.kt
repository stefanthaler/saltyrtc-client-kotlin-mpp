package org.saltyrtc.client.signalling.messages

import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce

class NewInitiatorMessage: IncomingSignallingMessage {
    override val TYPE: String = "new-initiator"

    constructor(nonce: Nonce, payloadMap: Map<String, Any>):super(nonce, payloadMap) {
        //TODO validate reason number
    }

    override fun validateSource(clientRole: SaltyRTCClient.Role) {
        TODO("Not yet implemented")
    }
}