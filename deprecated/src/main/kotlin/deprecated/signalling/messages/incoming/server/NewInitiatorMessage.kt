package deprecated.signalling.messages.incoming.server

import deprecated.DeprecatedSaltyRTCClient
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce

class NewInitiatorMessage: IncomingSignallingMessage {
    override val type  = Type( "new-initiator")

    constructor(nonce: Nonce, client: DeprecatedSaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce,client, payloadMap) {
        //TODO validate reason number
    }

    override fun validate(client: DeprecatedSaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }
}