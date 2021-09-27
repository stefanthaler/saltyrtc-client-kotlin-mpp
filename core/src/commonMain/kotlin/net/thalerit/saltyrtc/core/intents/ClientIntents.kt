package net.thalerit.saltyrtc.core.intents

import kotlinx.coroutines.CancellableContinuation
import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.crypto.PublicKey

sealed class ClientIntent {
    data class Connect(
        val isInitiator: Boolean,
        val path: SignallingPath,
        val task: Task<out Connection>,
        val webSocket: (Server) -> WebSocket,
        val continuation: CancellableContinuation<Result<Connection>>,
        val otherPermanentPublicKey: PublicKey?
    ) : ClientIntent()

    data class MessageReceived(val message: Message) : ClientIntent()
    data class SendMessage(val message: Message) : ClientIntent()
}