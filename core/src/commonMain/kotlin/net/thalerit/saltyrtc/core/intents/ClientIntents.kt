package net.thalerit.saltyrtc.core.intents

import net.thalerit.crypto.PublicKey
import net.thalerit.saltyrtc.api.*

sealed class ClientIntent {
    data class Connect(
        val isInitiator: Boolean,
        val path: SignallingPath,
        val task: SupportedTask,
        val webSocket: (Server) -> WebSocket,
        val otherPermanentPublicKey: PublicKey?
    ) : ClientIntent()

    data class MessageReceived(val message: Message) : ClientIntent()
    data class SendMessage(val message: Message) : ClientIntent()
}