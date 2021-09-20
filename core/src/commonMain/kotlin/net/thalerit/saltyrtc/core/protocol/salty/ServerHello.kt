package net.thalerit.saltyrtc.core.protocol.salty

import net.thalerit.saltyrtc.api.Message
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.ClientServerAuthState
import net.thalerit.saltyrtc.core.entity.messages.server.serverHelloMessage
import net.thalerit.saltyrtc.core.entity.nonce
import net.thalerit.saltyrtc.core.entity.withIncreasedSequenceNumber
import net.thalerit.saltyrtc.core.state.ServerIdentity
import net.thalerit.saltyrtc.crypto.sharedKey


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