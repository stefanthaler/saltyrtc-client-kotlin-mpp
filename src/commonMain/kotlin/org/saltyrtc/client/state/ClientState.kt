package org.saltyrtc.client.state

import org.saltyrtc.client.Cookie
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

fun initialClientState(): ClientState {
    return ClientState(
        isConnected = false,
        isInitiator = false,
        socket = null,
        authState = ClientServerAuthState.UNAUTHENTICATED,
        sessionSharedKey = null,
        sessionPublicKey = null,
        sessionCookie = null,
        nonces = mapOf(),
        identity = null,
        responders = mapOf(),
        clientAuthStates = mapOf(),
    )
}

data class ClientState(
    val isConnected: Boolean,
    val isInitiator: Boolean,
    val socket: WebSocket?,
    val authState: ClientServerAuthState,
    val sessionSharedKey: SharedKey?,
    val sessionPublicKey: PublicKey?,
    val sessionCookie: Cookie?,
    val identity: Identity?,
    val nonces: Map<Identity, Nonce>,
    val responders: Map<Identity, LastMessageSentTimeStamp>,
    val clientAuthStates: Map<Identity, ClientClientAuthState>
)

@JvmInline
value class LastMessageSentTimeStamp(val time: Long)
