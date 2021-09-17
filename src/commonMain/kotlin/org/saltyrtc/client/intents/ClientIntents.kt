package org.saltyrtc.client.intents

import org.saltyrtc.client.api.Message
import org.saltyrtc.client.api.SignallingPath
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.entity.Task

sealed class ClientIntent {
    data class Connect(
        val isInitiator: Boolean,
        val path: SignallingPath,
        val task: Task,
        val otherPermanentPublicKey: PublicKey?
    ) : ClientIntent()

    data class MessageReceived(val message: Message) : ClientIntent()
    data class SendMessage(val message: Message) : ClientIntent()
}