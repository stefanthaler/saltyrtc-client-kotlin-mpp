package org.saltyrtc.client.signalling.messages

import SaltyRTCClient
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce

class SendError:IncomingSignallingMessage {
    override val TYPE: String = "send-error"
    lateinit var id: ByteArray

    constructor(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce,client, payloadMap) {
        id = (payloadMap["id"] as ByteArray)!!
        //TODO validate reason number
    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }
}