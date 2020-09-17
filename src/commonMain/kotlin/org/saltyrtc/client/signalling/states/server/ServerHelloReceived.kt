package org.saltyrtc.client.signalling.states.server

import SaltyRTCClient
import org.saltyrtc.client.crypto.NaCl
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.states.BaseState
import org.saltyrtc.client.signalling.messages.outgoing.ClientAuthMessage
import org.saltyrtc.client.signalling.messages.incoming.server.authentication.ServerAuthMessage
import org.saltyrtc.client.signalling.messages.outgoing.ClientHelloMessage
import kotlin.reflect.KClass

/**
 * After the Initiator has received the Server hello message, a client-auth message will be sent and a server-auth message will be expected.
 *
 * The message SHALL be NaCl public-key encrypted:
 *  by the server's session key pair (public key sent in 'server-hello')
 *  the client's permanent key pair (public key as part of the WebSocket path or sent in 'client-hello').
 *
 *  PreviousState: StartState
 *  NextState:
 */
class ServerHelloReceived(client: SaltyRTCClient) : BaseState<ServerAuthMessage>(client) {

    override suspend fun stateActions() {
        client.server.identity=getIncomingMessage().nonce.destination
    }

    override suspend fun sendNextProtocolMessage() {
        //NO message
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