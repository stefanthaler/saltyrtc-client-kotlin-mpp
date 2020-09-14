package org.saltyrtc.client.signalling.states.initiator

import SaltyRTCClient
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.State
import org.saltyrtc.client.signalling.messages.ServerAuthMessage
import org.saltyrtc.client.signalling.messages.ServerHelloMessage
import kotlin.reflect.KClass

class ServerAuthReceived(client: SaltyRTCClient) : BaseState(client) {
    override val acceptedMessageType = ServerAuthMessage::class

    override suspend fun sendNextProtocolMessage() {
        TODO("Not yet implemented")
    }

    override suspend fun stateActions(message: IncomingSignallingMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun nextState(): State {
        TODO("Not yet implemented")
    }

    override fun isAuthenticated(): Boolean {
        TODO("Not yet implemented")
    }
}