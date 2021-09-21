package net.thalerit.saltyrtc.core.intents

import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.ClientServerAuthState
import net.thalerit.saltyrtc.core.entity.webSocketMessage

internal fun SaltyRtcClient.connect(intent: ClientIntent.Connect) {
    messageSupervisor.cancelChildren()
    current.socket?.close()

    val socket = intent.webSocket(signallingServer)
    messageScope.launch {
        socket.message
            .collect {
                queue(ClientIntent.MessageReceived(webSocketMessage(it)))
            }
    }

    current = current.copy(
        socket = socket,
        isInitiator = intent.isInitiator,
        authState = ClientServerAuthState.UNAUTHENTICATED,
        task = intent.task,
        continuation = intent.continuation,
        otherPermanentPublicKey = intent.otherPermanentPublicKey
    )

    socket.open(intent.path)
}