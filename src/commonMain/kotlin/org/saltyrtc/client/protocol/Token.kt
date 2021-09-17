package org.saltyrtc.client.protocol

import org.saltyrtc.client.SaltyRtcClient
import org.saltyrtc.client.entity.messages.client.oneTimeTokenMessage
import org.saltyrtc.client.intents.ClientIntent

/**
 *  Before the client-to-client handshake can take place, the initiator SHALL issue a token which is a securely random
 *  generated NaCl secret key (32 bytes) that is valid for a single successfully decrypted message – the 'token' message.
 *  The token MUST be exchanged securely between initiator and responder.
 *
 *  This specification deliberately does not define how the token should be exchanged.
 *
 *  // TODO look how other clients deliver this one time tokens
 */
internal fun SaltyRtcClient.sendOneTimeToken() {
    require(current.initiatorShouldSendToken)
    val responders = current.responders
    requireNotNull(responders)
    responders
        .keys
        .map { oneTimeTokenMessage(it) }
        .map { ClientIntent.SendMessage(it) }
        .forEach { queue(it) }
}