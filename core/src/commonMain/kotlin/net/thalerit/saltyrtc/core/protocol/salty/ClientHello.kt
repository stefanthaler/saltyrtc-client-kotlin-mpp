package net.thalerit.saltyrtc.core.protocol.salty

import net.thalerit.saltyrtc.api.Nonce
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.messages.server.clientHelloMessage
import net.thalerit.saltyrtc.core.intents.ClientIntent
import net.thalerit.saltyrtc.core.logging.logDebug


/**
 * As soon as the client has received the 'server-hello' message, it MUST ONLY respond with this message in case the
 * client takes the role of a responder. The initiator MUST skip this message.
 * The responder MUST set the public key (32 bytes) of the permanent key pair in the key field of this message.
 *
 * A receiving server MUST check that the message contains a valid NaCl public key (the size of the key MUST be exactly 32 bytes).
 * Note that the server does not know whether the client will send a 'client-hello' message (the client is a responder)
 * or a 'client-auth' message (the client is the initiator). Therefore, the server MUST be prepared to handle both message
 * types at that particular point in the message flow. This is also the intended way to differentiate between initiator and responder.
 *
 * The message SHALL NOT be encrypted.
 */

internal fun SaltyRtcClient.sendClientHello(nonce: Nonce) {
    logDebug("[$debugName] sending 'client-hello' message")
    require(!current.isInitiator)
    val message = clientHelloMessage(ownPermanentKey.publicKey, nonce)
    queue(ClientIntent.SendMessage(message))
}