package org.saltyrtc.client.protocol.salty

import org.saltyrtc.client.SaltyRtcClient
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.crypto.sharedKey
import org.saltyrtc.client.entity.ClientServerAuthState
import org.saltyrtc.client.entity.messages.server.serverHelloMessage
import org.saltyrtc.client.entity.nonce
import org.saltyrtc.client.entity.withIncreasedSequenceNumber
import org.saltyrtc.client.state.ServerIdentity

/**
 * This message MUST be sent by the server after a client connected to the server using a valid signalling path.
 * The server MUST generate a new cryptographically secure random NaCl key pair for each client.
 * The public key (32 bytes) of that key pair MUST be set in the key field of this message.
 *
 * A receiving client MUST check that the message contains a valid NaCl public key (the size of the key MUST be
 * exactly 32 bytes). In case the client has knowledge of the server's public permanent key, it SHALL ensure that the
 * server's public session key is different to the server's public permanent key.
 *
 * The message SHALL NOT be encrypted.
 */

internal fun SaltyRtcClient.handleServerHello(it: Message) {
    val message = serverHelloMessage(it)
    require(message.key != signallingServer.permanentPublicKey)

    val nonce = nonce()

    val sharedKeys = current.sessionSharedKeys.toMutableMap().apply {
        put(ServerIdentity, sharedKey(ownPermanentKey.privateKey, message.key))
    }

    current = current.copy(
        authState = ClientServerAuthState.SERVER_AUTH,
        sessionSharedKeys = sharedKeys,
        serverSessionPublicKey = message.key,
        serverSessionNonce = nonce
    )

    if (current.isInitiator) {
        sendClientAuth(nonce, it.nonce)
    } else {
        sendClientHello(nonce)
        val nextNonce = nonce.withIncreasedSequenceNumber()
        sendClientAuth(nextNonce, it.nonce)
    }
}