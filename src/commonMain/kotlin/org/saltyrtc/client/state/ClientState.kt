package org.saltyrtc.client.state

interface ClientState {
    val isConnected:Boolean
}

fun InitialClientState():ClientState {
    return ClientStateImpl(
        isConnected=false,
    )

}

data class ClientStateImpl(
    override val isConnected:Boolean
) : ClientState
