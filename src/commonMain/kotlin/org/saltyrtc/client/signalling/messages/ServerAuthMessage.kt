package org.saltyrtc.client.signalling.messages

import org.saltyrtc.client.crypto.NaClKey
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.SignallingMessageTypes

class ServerAuthMessage: IncomingSignallingMessage {
    override val TYPE: String = SignallingMessageTypes.SERVER_AUTH.type

    constructor(nonce: Nonce, payloadMap: Map<String, Any>) : super(nonce, payloadMap) {
        //TODO validate all values
    }

    override fun validateSource(clientRole: SaltyRTCClient.Role) {
        TODO("Not yet implemented")
    }
}