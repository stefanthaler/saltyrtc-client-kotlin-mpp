package net.thalerit.saltyrtc.core.protocol.salty

import net.thalerit.crypto.NaClConstants
import net.thalerit.crypto.PublicKey
import net.thalerit.crypto.SharedKey
import net.thalerit.saltyrtc.api.Identity
import net.thalerit.saltyrtc.api.Message
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.messages.client.oneTimeTokenMessage
import net.thalerit.saltyrtc.core.entity.unpack
import net.thalerit.saltyrtc.core.intents.ClientIntent
import net.thalerit.saltyrtc.crypto.secureRandomBytes


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
    val responders = current.responders
    requireNotNull(responders)

    val nonces = current.receivingNonces.toMutableMap()
    val tokens = mutableMapOf<Identity, ByteArray>()

    val sendIntents = responders
        .keys
        .map {
            val oneTimeToken = secureRandomBytes(NaClConstants.SYMMETRIC_KEY_BYTES)
            val message = oneTimeTokenMessage(it, SharedKey(oneTimeToken)) // TODO server could inject wrong public key
            nonces[it] = message.nonce
            tokens[it] = oneTimeToken
            message
        }
        .map { ClientIntent.SendMessage(it) }

    current = current.copy(
        receivingNonces = nonces,
        //tokens = tokens TODO
    )

    sendIntents.forEach { queue(it) }
}

/**
 * Once a responder has authenticated itself towards the server and an initiator is present on that path, it SHALL ONLY
 * send this message to the initiator in case it holds an authentication token issued by the initiator on that path.
 * This message SHALL be skipped in case the responder knows that the initiator already trusts it and previously stored
 * the responder's public key.

 * The responder MUST set the public key (32 bytes) of the permanent key pair in the key field of this message.

 * A receiving initiator MUST check that the message contains a valid NaCl public key (32 bytes) in the key field.
 * In case the initiator expects a 'token' message but could not decrypt the message's content, it SHALL send a
 * 'drop-responder' message containing the id of the responder who sent the message and a close code of 3005
 * (Initiator Could Not Decrypt) in the reason field.

 * The message SHALL be NaCl secret key encrypted by the token the initiator created and issued to the responder.
 * In case the initiator has successfully decrypted the 'token' message, the secret key MUST be invalidated immediately
 * and SHALL NOT be used for any other message.
 */
internal fun SaltyRtcClient.sendPublicKeyToken(
    responderPermanentPublicKey: PublicKey,
    oneTimeToken: SharedKey
) {
    // TODO
}

internal fun SaltyRtcClient.handleTokenMessage(it: Message) {
    if (current.isInitiator) {

    } else {
        val payloadMap = unpack(it.data)
    }
}