package net.thalerit.saltyrtc.core.protocol

import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.clearInitiatorPath
import net.thalerit.saltyrtc.core.clearResponderPath
import net.thalerit.saltyrtc.core.entity.messages.server.disconnectedMessage
import net.thalerit.saltyrtc.core.util.requireInitiatorId
import net.thalerit.saltyrtc.core.util.requireResponderId


/**
 * If an initiator that has been authenticated towards the server terminates the connection with the server, the server
 * SHALL send this message towards all connected and authenticated responders.

 * If a responder that has been authenticated towards the server terminates the connection with the server, the server
 * SHALL send this message towards the initiator (if present).

 * An initiator who receives a 'disconnected' message SHALL validate that the id field contains a valid responder address
 * (0x02..0xff).

 * A responder who receives a 'disconnected' message SHALL validate that the id field contains a valid initiator address
 * (0x01).

 * A receiving client MUST delete all cached information about and for the other client with the identity of the id field
 * (such as cookies and sequence numbers). The client MAY stay on the path and wait for a new initiator/responder to connect.
 * However, the client-to-client handshake MUST start from the beginning. In addition, the client MUST notify the user
 * application that the client with the identity id has disconnected.

 * The message SHALL be NaCl public-key encrypted by the server's session key pair and the client's permanent key pair.
 */
internal fun SaltyRtcClient.handleDisconnected(payloadMap: Map<MessageField, Any>) {
    val message = disconnectedMessage(payloadMap)
    if (current.isInitiator) {
        requireResponderId(message.id)
        clearResponderPath(message.id)
    } else {
        requireInitiatorId(message.id)
        clearInitiatorPath()
    }
}