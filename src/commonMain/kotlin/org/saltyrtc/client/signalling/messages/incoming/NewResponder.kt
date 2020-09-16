package org.saltyrtc.client.signalling.messages.incoming

import SaltyRTCClient
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.Nonce
import org.saltyrtc.client.signalling.SignallingMessageFields


/**
 * As soon as a new responder has authenticated itself towards the server on path, the server MUST send this message to an authenticated initiator on the same path
 */
class NewResponder:IncomingSignallingMessage {
    override val TYPE: String = "new-responder"
    var id: Byte = 0

    constructor(nonce: Nonce, client: SaltyRTCClient, payloadMap: Map<String, Any>) : super(nonce,client, payloadMap) {
        id = (payloadMap[SignallingMessageFields.ID] as Byte)!! // The field id MUST be set to the assigned identity of the newly connected responder.
        //TODO validate reason number
    }

    override fun validate(client: SaltyRTCClient, payloadMap: Map<String, Any>) {
        // An initiator who receives a 'new-responder' message SHALL validate that the id field contains a valid responder address (0x02..0xff)
        require (SignallingMessageFields.ID in payloadMap.keys)
        val id = payloadMap.get(SignallingMessageFields.ID)
        require( id != null)
        require( id is Byte)
        require( id.toInt() in 2..255 )

    }
}