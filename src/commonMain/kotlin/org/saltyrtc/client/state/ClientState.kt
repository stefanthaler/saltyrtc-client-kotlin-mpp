package org.saltyrtc.client.state

import org.saltyrtc.client.WebSocket
import org.saltyrtc.client.api.Nonce
import org.saltyrtc.client.api.requireResponderId
import org.saltyrtc.client.crypto.NaClKeyPair
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.crypto.SharedKey
import org.saltyrtc.client.entity.*
import org.saltyrtc.client.logging.logDebug
import kotlin.jvm.JvmInline

@JvmInline
value class Identity(val address: Byte)

val ServerIdentity = Identity(address = 0)
val InitiatorIdentity = Identity(address = 1)

fun initialClientState(): ClientState {
    return ClientState(
        isInitiator = false,
        socket = null,
        authState = ClientServerAuthState.UNAUTHENTICATED,
        serverSessionPublicKey = null, // session key
        serverSessionNonce = null, // cookie client -> server
        receivingNonces = mapOf(),
        sendingNonces = mapOf(),
        identity = null,
        responders = null,
        isInitiatorConnected = null,
        clientAuthStates = mapOf(),
        sessionSharedKeys = mapOf(),
        otherPermanentPublicKey = null,
        sessionOwnKeyPair = mapOf(),
        task = null
    )
}

data class ClientState(
    val isInitiator: Boolean,
    val otherPermanentPublicKey: PublicKey?,
    val socket: WebSocket?,
    val authState: ClientServerAuthState,
    val serverSessionPublicKey: PublicKey?,
    val serverSessionNonce: Nonce?,
    val identity: Identity?,
    val receivingNonces: Map<Identity, Nonce>, // contains cookies when receiving message from other peers
    val sendingNonces: Map<Identity, Nonce>, // contains cookies to send messages to other peers
    val responders: Map<Identity, LastMessageSentTimeStamp>?,
    val isInitiatorConnected: Boolean?,
    val clientAuthStates: Map<Identity, ClientClientAuthState>,
    val sessionSharedKeys: Map<Identity, SharedKey>,
    val sessionOwnKeyPair: Map<Identity, NaClKeyPair>,
    val task: Task?,
) {
    val serverSessionSharedKey: SharedKey? by lazy {
        sessionSharedKeys[ServerIdentity]
    }

    val isResponder: Boolean by lazy {
        !isInitiator
    }

    val responderShouldSendKey: Boolean
        get() {
            logDebug("Responder should send key: $isResponder $isInitiatorConnected $otherPermanentPublicKey")
            return isResponder && isInitiatorConnected == true && otherPermanentPublicKey != null
        }

    fun initiatorShouldSendToken(responder: Identity): Boolean { // TODO
        requireResponderId(responder)
        return isResponder && !responders.isNullOrEmpty() && otherPermanentPublicKey == null
    }

    fun nextSendingNonce(destination: Identity): Nonce {
        val ownId = identity
        requireNotNull(ownId)

        val nextNonce = sendingNonces[destination] ?: nonce(ownId, destination)
        return nextNonce.withIncreasedSequenceNumber()
    }
}

@JvmInline
value class LastMessageSentTimeStamp(val time: Long)
