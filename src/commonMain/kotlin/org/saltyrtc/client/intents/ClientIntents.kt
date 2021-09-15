package org.saltyrtc.client.intents

sealed class ClientIntent {
    data class Connect(val isInitiator: Boolean):ClientIntent()
}