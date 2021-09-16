package org.saltyrtc.client.state

import org.saltyrtc.client.WebSocket


fun initialClientState():ClientState {
    return ClientState(
        isConnected=false,
        isInitiator=false,
        socket = null,
    )
}

data class ClientState(
     val isConnected:Boolean,
     val isInitiator: Boolean,
     val socket: WebSocket?,
)
