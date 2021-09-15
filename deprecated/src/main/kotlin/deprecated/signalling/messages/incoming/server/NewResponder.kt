package deprecated.signalling.messages.incoming.server

import deprecated.DeprecatedSaltyRTCClient
import org.saltyrtc.client.signalling.messages.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.messages.SignallingMessageFields


/**
 * As soon as a new responder has authenticated itself towards the server on path, the server MUST send this message to an authenticated initiator on the same path
 */
class NewResponder: IncomingSignallingMessage {
    override val type  = Type( "new-responder" )
    var id: Byte = 0

    constructor(nonce: Nonce, client: DeprecatedSaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce,client, payloadMap) {
        id = (payloadMap[SignallingMessageFields.ID.value] as Byte) // The field id MUST be set to the assigned identity of the newly connected responder.
        //TODO validate reason number
    }

    override fun validate(client: DeprecatedSaltyRTCClient, payloadMap: Map<String, Any>) {
        // An initiator who receives a 'new-responder' message SHALL validate that the id field contains a valid responder address (0x02..0xff)

        require (SignallingMessageFields.ID.value in payloadMap.keys)
        val id = payloadMap[SignallingMessageFields.ID.value]
        require( id != null)
        require( id is Byte)
        require( id.toInt() in 2..255 )
    }
}