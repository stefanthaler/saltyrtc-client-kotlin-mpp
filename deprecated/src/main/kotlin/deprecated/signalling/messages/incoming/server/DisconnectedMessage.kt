package deprecated.signalling.messages.incoming.server

import deprecated.DeprecatedSaltyRTCClient
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce

class DisconnectedMessage: IncomingSignallingMessage {
    override val type = Type( "disconnected" )
    var id:Byte=0

    constructor(nonce: Nonce, client: DeprecatedSaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce,client, payloadMap) {
        id = (payloadMap["id"] as Byte)
        //TODO validate byte number

    }

    override fun validate(client: DeprecatedSaltyRTCClient, payloadMap: Map<String, Any>) {
        TODO("Not yet implemented")
    }
}