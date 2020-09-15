package org.saltyrtc.client.signalling.messages

import org.saltyrtc.client.crypto.NaClKey
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.SignallingMessageTypes

/**
 * Once the server has received the 'client-auth' message, it SHALL reply with this message. Depending on the client's role, the server SHALL choose and assign an identity to the client by setting the destination address accordingly:
 */
class ServerAuthMessage: IncomingSignallingMessage {
    override val TYPE: String = SignallingMessageTypes.SERVER_AUTH.type
    val HELLP:String="fdf"

    constructor(nonce: Nonce, payloadMap: Map<String, Any>, clientRole: SaltyRTCClient.Role) : super(nonce, payloadMap) {
        

        //TODO validate all values
    }

    override fun validateSource(clientRole: SaltyRTCClient.Role) {
        TODO("Not yet implemented")
    }
}