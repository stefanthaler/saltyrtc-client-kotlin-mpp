package org.saltyrtc.client.state

import org.saltyrtc.client.WebSocket
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.entity.ClientServerAuthState


fun initialClientState(): ClientState {
    return ClientState(
        isConnected = false,
        isInitiator = false,
        socket = null,
        authState = ClientServerAuthState.DISCONNECTED,
        sessionKey = null
    )
}

data class ClientState(
    val isConnected: Boolean,
    val isInitiator: Boolean,
    val socket: WebSocket?,
    val authState: ClientServerAuthState,
    val sessionKey: PublicKey?
)
