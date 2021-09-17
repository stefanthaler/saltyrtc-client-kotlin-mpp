package org.saltyrtc.client.protocol

import org.saltyrtc.client.SaltyRtcClient
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.close
import org.saltyrtc.client.entity.CloseReason
import org.saltyrtc.client.entity.messages.client.closeMessage
import org.saltyrtc.client.intents.ClientIntent
import org.saltyrtc.client.state.Identity
import org.saltyrtc.client.state.initialClientState
import org.saltyrtc.client.state.nextSendingNonce

/**
 * Both initiator and responder SHALL trigger sending this message any time the application or a task requests to
 * terminate the signalling connection between the clients over the server and to the server. However, this message SHALL
 * ONLY be sent in case the client-to-client handshakes has been completed

 * A client who sends a 'close' message MUST set the reason field to a valid close code (as enumerated in Close Code Enumeration).
 * 1001 SHALL be used for normal close cases. Once the message has been sent, the client SHALL remove all cached data
 * (such as cookies and sequence numbers) of and for the other client. The client SHALL also terminate the connection
 * to the server with a close code of 1001 (Going Away) if the connection is still open.

 * A receiving client SHALL validate that the reason field contains a valid close code (as enumerated in Close Code
 * Enumeration). The client SHALL remove all cached data (such as cookies and sequence numbers) of and for the other
 * client. The client SHALL also terminate the connection to the server (no specific close code) if the connection is still open.

 * The message SHALL be NaCl public-key encrypted by the client's session key pair and the other client's session key pair.
 */
internal fun SaltyRtcClient.handleClose(it: Message) {
    val source = it.nonce.source
    val sessionSharedKey = current.sessionSharedKeys[source]
    requireNotNull(sessionSharedKey)
    closeMessage(it, sessionSharedKey)
    current = initialClientState()
    close()
}

internal fun SaltyRtcClient.sendClose(destination: Identity, reason: CloseReason) {
    val nonce = current.nextSendingNonce(destination)
    val sessionSharedKey = current.sessionSharedKeys[destination]!!
    val message = closeMessage(reason, sessionSharedKey, nonce)
    queue(ClientIntent.SendMessage(message))
}