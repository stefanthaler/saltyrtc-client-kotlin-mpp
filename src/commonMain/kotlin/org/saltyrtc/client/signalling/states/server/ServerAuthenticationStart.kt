package org.saltyrtc.client.signalling.states.server

import SaltyRTCClient
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.messages.incoming.server.authentication.ServerHelloMessage
import org.saltyrtc.client.signalling.states.BaseState
import kotlin.reflect.KClass

class ServerAuthenticationStart(client: SaltyRTCClient) : BaseState<ServerHelloMessage>(client) {
    override suspend fun sendNextProtocolMessage() {
        throw ValidationError("SendNextMessage should not be called from start state - after connecting the WebSocket, the server should send a ServerHello message.")
    }

    override fun isAuthenticated(): Boolean {
        return false
    }

    override suspend fun stateActions() {
        client.sessionPublicKey = getIncomingMessage().key
    }

    override suspend fun setNextState() {
        client.state= ServerHelloReceived(client)
    }

    override fun allowedMessageTypes(): Array<KClass<out ServerHelloMessage>> {
        return arrayOf(ServerHelloMessage::class)
    }
}