package org.saltyrtc.client.state

import org.saltyrtc.client.Nonce
import org.saltyrtc.client.WebSocket
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.crypto.SharedKey
import org.saltyrtc.client.entity.ClientClientAuthState
import org.saltyrtc.client.entity.ClientServerAuthState
import kotlin.jvm.JvmInline

@JvmInline
value class Identity(val address: Byte)

val ServerIdentity = Identity(address = 0)
val InitiatorIdentity = Identity(address = 1)

fun initialClientState(otherPermanentPublicKey: PublicKey?): ClientState {
    return ClientState(
        isInitiator = false,
        otherPermanentPublicKey = otherPermanentPublicKey,
        socket = null,
        authState = ClientServerAuthState.UNAUTHENTICATED,
        serverSessionPublicKey = null, // session key
        serverSessionNonce = null, // cookie client -> server
        nonces = mapOf(),
        identity = null,
        responders = null,
        isInitiatorConnected = null,
        clientAuthStates = mapOf(),
        sessionSharedKeys = mapOf(),
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
    val nonces: Map<Identity, Nonce>, // contains cookies from other peers
    val responders: Map<Identity, LastMessageSentTimeStamp>?,
    val isInitiatorConnected: Boolean?,
    val clientAuthStates: Map<Identity, ClientClientAuthState>,
    val sessionSharedKeys: Map<Identity, SharedKey>
) {
    val serverSessionSharedKey: SharedKey? by lazy {
        sessionSharedKeys[ServerIdentity]
    }


    val isResponder: Boolean by lazy {
        !isInitiator
    }

    val isResponderShouldSendKey: Boolean by lazy {
        isResponder && isInitiatorConnected == true && otherPermanentPublicKey != null
    }
}

@JvmInline
value class LastMessageSentTimeStamp(val time: Long)
