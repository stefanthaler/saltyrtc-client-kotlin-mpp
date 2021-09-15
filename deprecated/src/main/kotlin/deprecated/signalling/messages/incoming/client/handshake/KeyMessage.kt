package deprecated.signalling.messages.incoming.client.handshake

import deprecated.DeprecatedSaltyRTCClient
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.messages.SignallingMessageTypes

class KeyMessage: TokenOrKeyMessage {
    override val type = Type(SignallingMessageTypes.KEY.type)

    constructor(nonce: Nonce, client: DeprecatedSaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce, client, payloadMap) {

    }

    override fun validate(client: DeprecatedSaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }
}