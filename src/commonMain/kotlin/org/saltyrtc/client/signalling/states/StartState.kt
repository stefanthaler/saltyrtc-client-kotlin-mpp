package org.saltyrtc.client.signalling.states

import SaltyRTCClient
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.*
import org.saltyrtc.client.signalling.messages.incoming.ServerHelloMessage
import org.saltyrtc.client.signalling.states.server.ServerHelloReceived

class StartState(client: SaltyRTCClient) : BaseState<ServerHelloMessage>(client) {
    override val acceptedMessageType = ServerHelloMessage::class

    override suspend fun sendNextProtocolMessage() {
        ValidationError("SendNextMessage should not be called from start state - after connecting the WebSocket, the server should send a ServerHello message.")
    }

    override fun isAuthenticatedTowardsServer(): Boolean {
        return false
    }

    override suspend fun stateActions(message: ServerHelloMessage) {
        client.sessionPublicKey = message.key
    }

    override suspend fun setNextState(message: ServerHelloMessage) {
        client.state= ServerHelloReceived(client)
    }
}