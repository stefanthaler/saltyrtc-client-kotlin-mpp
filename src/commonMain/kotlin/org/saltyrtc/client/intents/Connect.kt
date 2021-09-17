package org.saltyrtc.client.intents

import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.saltyrtc.client.SaltyRtcClient
import org.saltyrtc.client.entity.ClientServerAuthState
import org.saltyrtc.client.webSocket

internal fun SaltyRtcClient.connect(intent: ClientIntent.Connect) {
    messageSupervisor.cancelChildren()
    current.socket?.close()

    val socket = webSocket(signallingServer)
    messageScope.launch {
        socket.message.collect {
            queue(ClientIntent.MessageReceived(it))
        }
    }

    current = current.copy(
        socket = socket,
        isInitiator = intent.isInitiator,
        authState = ClientServerAuthState.UNAUTHENTICATED,
        task = intent.task,
        otherPermanentPublicKey = intent.otherPermanentPublicKey
    )

    socket.open(intent.path)
}