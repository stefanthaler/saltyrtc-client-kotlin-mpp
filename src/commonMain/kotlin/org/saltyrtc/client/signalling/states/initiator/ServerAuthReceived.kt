package org.saltyrtc.client.signalling.states.initiator

import SaltyRTCClient
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.BaseState
import org.saltyrtc.client.signalling.IncomingSignallingMessage
import org.saltyrtc.client.signalling.State
import org.saltyrtc.client.signalling.messages.ServerAuthMessage
import org.saltyrtc.client.signalling.messages.ServerHelloMessage
import kotlin.reflect.KClass

class ServerAuthReceived(client: SaltyRTCClient) : BaseState<ServerAuthMessage>(client) {
    override val acceptedMessageType = ServerAuthMessage::class

    override suspend fun validate(message: ServerAuthMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun stateActions(message: ServerAuthMessage) {
       if(client.role==SaltyRTCClient.Role.INITIATOR && message.nonce.destination.toInt()!=1) {
           throw ValidationError("Initiator should be assigned destination 0x01, was ${message.nonce.destination}")
       }
        if(client.role==SaltyRTCClient.Role.RESPONDER && message.nonce.destination.toInt() !in (2..255) ) {
            throw ValidationError("Resonder should be assigned destination 0x02 - 0xFF, was ${message.nonce.destination}")
        }

        // When the client receives a 'server-auth' message, it MUST have accepted and set its identity as described in the Receiving a Signalling Message section. This identity is valid until the connection has been severed
        client.identity=message.nonce.destination

    }

    override suspend fun setNextState(message: ServerAuthMessage) {
        TODO("Not yet implemented")
    }

    override suspend fun sendNextProtocolMessage() {
        TODO("Not yet implemented")
    }

    override fun isAuthenticated(): Boolean {
        TODO("Not yet implemented")
    }

}