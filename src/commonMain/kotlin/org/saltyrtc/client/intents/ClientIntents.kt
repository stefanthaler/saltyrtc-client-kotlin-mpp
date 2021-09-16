package org.saltyrtc.client.intents

import org.saltyrtc.client.SignallingPath

sealed class ClientIntent {
    data class Connect(
        val isInitiator: Boolean,
        val path:SignallingPath,
    ):ClientIntent()
}