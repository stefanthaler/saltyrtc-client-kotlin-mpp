package org.saltyrtc.client.signalling.states.server

import SaltyRTCClient
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.messages.incoming.ServerAuthMessage

class ResponderAuthenticatedTowardsServer(client: SaltyRTCClient) : BaseState<ServerAuthMessage>(client) {
    override val acceptedMessageType = ServerAuthMessage::class

    override suspend fun sendNextProtocolMessage() {
        TODO("Not yet implemented")
    }

    override suspend fun stateActions(message: ServerAuthMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun setNextState(message: ServerAuthMessage) {
        TODO("Not yet implemented")
    }

    override fun isAuthenticatedTowardsServer(): Boolean {
        return true
    }
}