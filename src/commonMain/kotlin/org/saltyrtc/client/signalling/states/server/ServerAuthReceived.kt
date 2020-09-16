package org.saltyrtc.client.signalling.states.server

import SaltyRTCClient
import org.saltyrtc.client.signalling.states.BaseState
import org.saltyrtc.client.signalling.messages.incoming.server.authentication.ServerAuthMessage
import kotlin.reflect.KClass

class ServerAuthReceived(client: SaltyRTCClient) : BaseState<ServerAuthMessage>(client) {
    override suspend fun sendNextProtocolMessage() {
        TODO("Not yet implemented")
    }

    override suspend fun stateActions() {
        // The first message received with a destination address different to 0x00 SHALL be accepted as the client's assigned identity.
    }

    override suspend fun setNextState() {
        if (client.isInitiator()) {
            client.state = InitiatorAuthenticatedTowardsServer(client)
        } else {
            client.state = ResponderAuthenticatedTowardsServer(client)
        }
    }

    override fun isAuthenticated(): Boolean {
        return false
    }

    override fun allowedMessageTypes(): Array<KClass<out ServerAuthMessage>> {
        return arrayOf(ServerAuthMessage::class)
    }

}