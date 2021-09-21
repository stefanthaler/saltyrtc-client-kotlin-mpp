package net.thalerit.saltyrtc.core.intents

import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.ClientServerAuthState
import net.thalerit.saltyrtc.core.entity.webSocketMessage
import kotlin.coroutines.resume

internal fun SaltyRtcClient.connect(intent: ClientIntent.Connect) {
    messageSupervisor.cancelChildren()
    current.socket?.close()

    val socket = intent.webSocket(signallingServer)
    messageScope.launch {
        socket.message
            .collect {
                it.onSuccess { message ->
                    queue(ClientIntent.MessageReceived(webSocketMessage(message)))
                }
                it.onFailure { throwable ->
                    val cont = intent.continuation
                    if (intent.continuation.isActive) {
                        cont.resume(Result.failure(throwable))
                    } else {
                        println("Socket exception: $throwable")
                        //TODO handle - close?
                    }
                }
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