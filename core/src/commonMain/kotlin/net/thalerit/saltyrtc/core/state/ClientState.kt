package net.thalerit.saltyrtc.core.state

import net.thalerit.crypto.NaClKeyPair
import net.thalerit.crypto.PublicKey
import net.thalerit.crypto.SharedKey
import net.thalerit.saltyrtc.api.Identity
import net.thalerit.saltyrtc.api.WebSocket
import net.thalerit.saltyrtc.api.Nonce
import net.thalerit.saltyrtc.api.SupportedTask
import net.thalerit.saltyrtc.core.entity.ClientClientAuthState
import net.thalerit.saltyrtc.core.entity.ClientServerAuthState
import net.thalerit.saltyrtc.core.entity.nonce
import net.thalerit.saltyrtc.core.entity.withIncreasedSequenceNumber
import net.thalerit.saltyrtc.core.logging.logDebug
import net.thalerit.saltyrtc.core.util.requireResponderId
import kotlin.jvm.JvmInline

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
    val task: SupportedTask?,
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
}

fun ClientState.initiatorShouldSendToken(responder: Identity): Boolean { // TODO
    requireResponderId(responder)
    return isResponder && !responders.isNullOrEmpty() && otherPermanentPublicKey == null
}

fun ClientState.nextSendingNonce(destination: Identity): Nonce {
    val ownId = identity
    requireNotNull(ownId)

    val nextNonce = sendingNonces[destination] ?: nonce(ownId, destination)
    return nextNonce.withIncreasedSequenceNumber()
}


@JvmInline
value class LastMessageSentTimeStamp(val time: Long)

inline fun <K, V> Map<K, V>.mutableApply(block: MutableMap<K, V>.() -> Unit): Map<K, V> {
    return toMutableMap().apply(block = block)
}