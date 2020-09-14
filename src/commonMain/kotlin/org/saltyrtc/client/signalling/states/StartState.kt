package org.saltyrtc.client.signalling.states

import SaltyRTCClient
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.*
import org.saltyrtc.client.signalling.messages.ServerHelloMessage
import org.saltyrtc.client.signalling.states.initiator.ServerHelloReceived
import kotlin.reflect.KClass

class StartState(client: SaltyRTCClient) : BaseState(client) {
    override val acceptedMessageType = ServerHelloMessage::class

    override suspend fun sendNextProtocolMessage() {
        ValidationError("SendNextMessage should not be called from start state - after connecting the WebSocket, the server should send a ServerHello message.")
    }


    override suspend fun stateActions(message:IncomingSignallingMessage) {
        client.sessionPublicKey = (message as ServerHelloMessage).key
    }

    override suspend fun nextState(): State {
        return ServerHelloReceived(client)
    }

    override fun isAuthenticated(): Boolean {
        return false
    }


}