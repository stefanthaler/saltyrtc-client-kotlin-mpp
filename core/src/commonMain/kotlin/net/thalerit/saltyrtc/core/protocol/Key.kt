package net.thalerit.saltyrtc.core.protocol

import net.thalerit.saltyrtc.api.Identity
import net.thalerit.saltyrtc.api.Message
import net.thalerit.saltyrtc.api.Nonce
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.ClientClientAuthState
import net.thalerit.saltyrtc.core.entity.messages.client.clientSessionKeyMessage
import net.thalerit.saltyrtc.core.intents.ClientIntent
import net.thalerit.saltyrtc.core.state.nextSendingNonce
import net.thalerit.saltyrtc.core.util.requireAuthenticatedToServer
import net.thalerit.saltyrtc.core.util.requireInitiatorId
import net.thalerit.saltyrtc.core.util.requireResponderId
import net.thalerit.saltyrtc.crypto.PublicKey
import net.thalerit.saltyrtc.crypto.generateKeyPair
import net.thalerit.saltyrtc.crypto.sharedKey

/**
 * This message is sent by both initiator and responder. The responder SHALL send this message as its first message or
 * directly after the 'token' message. The initiator MUST wait until it has successfully processed the message before it
 * sends a 'key' message to that responder.
 *
 * The client MUST generate a session key pair (a new NaCl key pair for public key authenticated encryption) for further
 * communication with the other client. The client's session key pair SHALL NOT be identical to the client's permanent
 * key pair. It MUST set the public key (32 bytes) of that key pair in the key field.
 *
 * Once the other client receives a 'key' message, it MUST validate the key field: The key shall be 32 bytes and SHALL NOT
 * be identical to the other client's public permanent key. Further messages from the other client will use the session
 * key pair for encryption unless otherwise specified (e.g. by a task). In case an initiator expects a 'key' message but
 * could not decrypt the message's content, it SHALL send a 'drop-responder' message containing the id of the responder
 * who sent the message and a close code of 3005 (Initiator Could Not Decrypt) in the reason field.
 *
 * The message SHALL be NaCl public-key encrypted by the client's permanent key pair and the other client's permanent key pair.
 */
internal fun SaltyRtcClient.sendClientSessionKey(nonce: Nonce) {
    val destination = nonce.destination
    val otherPermanentPublicKey = current.otherPermanentPublicKey
    requireNotNull(otherPermanentPublicKey)
    requireAuthenticatedToServer(current)

    val sharedKey = sharedKey(ownPermanentKey.privateKey, otherPermanentPublicKey)
    val sessionKeyPair = generateKeyPair(ownPermanentKey)

    val message = clientSessionKeyMessage(sessionKeyPair.publicKey, sharedKey, nonce)

    val sendingNonces = current.sendingNonces.toMutableMap().apply {
        put(destination, nonce)
    }
    val sessionOwnKeyPairs = current.sessionOwnKeyPair.toMutableMap().apply {
        put(destination, sessionKeyPair)
    }

    current = current.copy(
        sendingNonces = sendingNonces,
        sessionOwnKeyPair = sessionOwnKeyPairs,
    )

    queue(ClientIntent.SendMessage(message))
}

internal fun SaltyRtcClient.handleClientSessionKeyMessage(it: Message) {
    val otherIdentity = it.nonce.source
    if (current.isInitiator) {
        requireResponderId(otherIdentity)
    } else {
        requireInitiatorId(otherIdentity)
    }
    val otherPermanentKey = current.otherPermanentPublicKey
    requireNotNull(otherPermanentKey)
    val permanentSharedKey = sharedKey(ownPermanentKey.privateKey, otherPermanentKey)
    val incomingMessage = clientSessionKeyMessage(it, permanentSharedKey, it.nonce)

    if (current.isInitiator) {
        val nextNonce = current.nextSendingNonce(otherIdentity)
        sendClientSessionKey(nextNonce)
    }
    updateSession(otherIdentity, incomingMessage.clientSession, it.nonce)
    if (current.isResponder) {
        sendResponderAuthMessage()
    }
}

private fun SaltyRtcClient.updateSession(
    identity: Identity,
    sessionOtherPublicKey: PublicKey,
    otherNonce: Nonce
) {
    val ownSessionKeyPair = current.sessionOwnKeyPair[identity]
    requireNotNull(ownSessionKeyPair)

    val sharedKey = sharedKey(
        ownSessionKeyPair.privateKey,
        sessionOtherPublicKey
    )
    val sessionSharedKeys = current.sessionSharedKeys.toMutableMap().apply {
        put(identity, sharedKey)
    }
    val clientAuthStates = current.clientAuthStates.toMutableMap().apply {
        put(identity, ClientClientAuthState.CLIENT_AUTH)
    }

    val nonces = current.receivingNonces.toMutableMap().apply {
        put(identity, otherNonce) // TODO validate
    }

    current = current.copy(
        sessionSharedKeys = sessionSharedKeys,
        clientAuthStates = clientAuthStates,
        receivingNonces = nonces,
    )
}