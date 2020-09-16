package org.saltyrtc.client.signalling.messages.incoming.server

import SaltyRTCClient
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce

class DisconnectedMessage: IncomingSignallingMessage {
    override val TYPE: String = "disconnected"
    var id:Byte=0

    constructor(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce,client, payloadMap) {
        id = (payloadMap["id"] as Byte)!!
        //TODO validate byte number

    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }

}