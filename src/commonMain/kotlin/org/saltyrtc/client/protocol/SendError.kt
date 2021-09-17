package org.saltyrtc.client.protocol

import org.saltyrtc.client.SaltyRtcClient
import org.saltyrtc.client.api.requireInitiatorId
import org.saltyrtc.client.api.requireResponderId
import org.saltyrtc.client.clearResponderPath
import org.saltyrtc.client.entity.messages.MessageField
import org.saltyrtc.client.entity.messages.sendErrorMessage

/**
 * In case the server could not relay a client-to-client message (meaning that the connection between server and the
 * receiver has been severed), the server MUST send this message to the original sender of the message that should have
 * been relayed. The server SHALL set the id field to the concatenation of the source address, the destination address,
 * the overflow number and the sequence number (or the combined sequence number) of the nonce section from the original
 * message.

 * A receiving client MUST treat this incident by raising an error event to the user's application and deleting all
 * cached information about and for the other client (such as cookies and sequence numbers). The client MAY stay on the
 * path and wait for a new initiator/responder to connect. However, the client-to-client handshake MUST start from the
 * beginning.

 * The message SHALL be NaCl public-key encrypted by the server's session key pair and the client's permanent key pair.
 */
internal fun SaltyRtcClient.handleSendError(payloadMap: Map<MessageField, Any>) {
    val message = sendErrorMessage(payloadMap)
    if (current.isInitiator) {
        requireResponderId(message.id)
    } else {
        requireInitiatorId(message.id)
    }
    clearResponderPath(message.id)
}