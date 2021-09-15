package deprecated.signalling.messages.incoming.client.handshake

import deprecated.DeprecatedSaltyRTCClient
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.messages.SignallingMessageTypes

abstract class TokenOrKeyMessage(nonce: Nonce, client: DeprecatedSaltyRTCClient, payloadMap: Map<String, Any>) : IncomingSignallingMessage(nonce, client,
    payloadMap) {
}

class TokenMessage: TokenOrKeyMessage {
    override val type = Type (SignallingMessageTypes.TOKEN.type)

    constructor(nonce: Nonce, client: DeprecatedSaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce, client, payloadMap) {

    }

    override fun validate(client: DeprecatedSaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }
}