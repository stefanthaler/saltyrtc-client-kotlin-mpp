package deprecated.signalling.messages.incoming.server

import deprecated.DeprecatedSaltyRTCClient
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce

class SendError: IncomingSignallingMessage {
    override val type = Type( "send-error")
    lateinit var id: ByteArray

    constructor(nonce: Nonce, client: DeprecatedSaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce,client, payloadMap) {
        id = (payloadMap["id"] as ByteArray)
        //TODO validate reason number
    }

    override fun validate(client: DeprecatedSaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }
}