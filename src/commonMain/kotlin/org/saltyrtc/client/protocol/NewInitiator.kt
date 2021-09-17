package org.saltyrtc.client.protocol

import org.saltyrtc.client.SaltyRtcClient
import org.saltyrtc.client.clearInitiatorPath

/**
 * When a new initiator has authenticated itself towards the server on a path, the server MUST send this message to all
 * currently authenticated responders on the same path. No additional field needs to be set. The server MUST ensure that a
 * 'new-initiator' message has been sent before the corresponding initiator is able to send messages to any responder.

 * A responder who receives a 'new-initiator' message MUST proceed by deleting all currently cached information about
 * and for the previous initiator (such as cookies and the sequence numbers) and continue by sending a 'token' or 'key'
 * client-to-client message described in the Client-to-Client Messages section.

 * The message SHALL be NaCl public-key encrypted by the server's session key pair and the responder's permanent key pair.
 */
internal fun SaltyRtcClient.handleNewInitiator() {
    require(!current.isInitiator)
    clearInitiatorPath()
}