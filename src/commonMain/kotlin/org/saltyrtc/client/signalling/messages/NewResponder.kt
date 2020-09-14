package org.saltyrtc.client.signalling.messages

import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce

class NewResponder:IncomingSignallingMessage {
    override val TYPE: String = "new-responder"
    var id: Byte = 0

    constructor(nonce: Nonce, payloadMap: Map<String, Any>):super(nonce, payloadMap) {
        id = (payloadMap["id"] as Byte)!!
        //TODO validate reason number
    }

    override fun validateSource(clientRole: SaltyRTCClient.Role) {
        TODO("Not yet implemented")
    }
}