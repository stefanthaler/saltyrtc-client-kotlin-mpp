package org.saltyrtc.client.signalling.states.server

import SaltyRTCClient
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.messages.incoming.ServerAuthMessage
import org.saltyrtc.client.signalling.messages.incoming.TokenMessage

class InitiatorAuthenticatedTowardsServer(client: SaltyRTCClient) : BaseState<TokenMessage>(client) {
    override val acceptedMessageTypes = TokenMessage::class

    override suspend fun sendNextProtocolMessage() {
        TODO("Not yet implemented")
    }

    override suspend fun stateActions(message: TokenMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun setNextState(message: TokenMessage) {
        TODO("Not yet implemented")
    }

    override fun isAuthenticatedTowardsServer(): Boolean {
        return true
    }
}