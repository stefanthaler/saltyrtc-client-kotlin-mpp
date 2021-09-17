package org.saltyrtc.client.protocol

import org.saltyrtc.client.SaltyRtcClient
import org.saltyrtc.client.api.requireResponderId
import org.saltyrtc.client.entity.ClientClientAuthState
import org.saltyrtc.client.entity.messages.MessageField
import org.saltyrtc.client.entity.messages.newResponderMessage
import org.saltyrtc.client.state.LastMessageSentTimeStamp
import org.saltyrtc.client.util.currentTimeInMs

/**
 * As soon as a new responder has authenticated itself towards the server on path, the server MUST send this message to
 * an authenticated initiator on the same path. The field id MUST be set to the assigned identity of the newly connected
 * responder. The server MUST ensure that a 'new-responder' message has been sent before the corresponding responder
 * is able to send messages to the initiator.

 * An initiator who receives a 'new-responder' message SHALL validate that the id field contains a valid responder
 * address (0x02..0xff). It SHOULD store the responder's identity in its internal list of responders.
 * If a responder with the same id already exists, all currently cached information about and for the previous responder
 * (such as cookies and the sequence number) MUST be deleted first. Furthermore, the initiator MUST keep its path clean
 * by following the procedure described in the Path Cleaning section.

 * The message SHALL be NaCl public-key encrypted by the server's session key pair and the initiator's permanent key pair.
 */
internal fun SaltyRtcClient.handleNewResponder(payloadMap: Map<MessageField, Any>) {
    require(current.isInitiator)
    val message = newResponderMessage(payloadMap)
    requireResponderId(message.id)

    val responders = current.responders
    requireNotNull(responders)

    current = current.copy(
        responders = responders.toMutableMap().apply {
            put(message.id, LastMessageSentTimeStamp(currentTimeInMs())) // TODO path cleaning
        },
        clientAuthStates = current.clientAuthStates.toMutableMap().apply {
            put(message.id, ClientClientAuthState.UNAUTHENTICATED)
        }
    )
}