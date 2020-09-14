package org.saltyrtc.client.signalling.messages

import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce

class SendError:IncomingSignallingMessage {
    override val TYPE: String = "send-error"
    lateinit var id: ByteArray

    constructor(nonce: Nonce, payloadMap: Map<String, Any>):super(nonce, payloadMap) {
        id = (payloadMap["id"] as ByteArray)!!
        //TODO validate reason number
    }

    override fun validateSource(clientRole: SaltyRTCClient.Role) {
        TODO("Not yet implemented")
    }
}