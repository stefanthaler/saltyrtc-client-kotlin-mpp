package org.saltyrtc.client.signalling.messages

import SaltyRTCClient
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce

class NewResponder:IncomingSignallingMessage {
    override val TYPE: String = "new-responder"
    var id: Byte = 0

    constructor(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce,client, payloadMap) {
        id = (payloadMap["id"] as Byte)!!
        //TODO validate reason number
    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }
}