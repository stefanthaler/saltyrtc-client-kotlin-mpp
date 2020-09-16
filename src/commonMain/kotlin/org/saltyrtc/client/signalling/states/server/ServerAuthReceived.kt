package org.saltyrtc.client.signalling.states.server

import SaltyRTCClient
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.messages.incoming.ServerAuthMessage

class ServerAuthReceived(client: SaltyRTCClient) : BaseState<ServerAuthMessage>(client) {
    override suspend fun sendNextProtocolMessage(message: ServerAuthMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun stateActions(message: ServerAuthMessage) {
        // The first message received with a destination address different to 0x00 SHALL be accepted as the client's assigned identity.
    }

    override suspend fun setNextState(message: ServerAuthMessage) {
        if (client.isInitiator()) {
            client.state = InitiatorAuthenticatedTowardsServer(client)
        } else {
            client.state = ResponderAuthenticatedTowardsServer(client)
        }
    }

    override fun isAuthenticated(): Boolean {
        return false
    }

}