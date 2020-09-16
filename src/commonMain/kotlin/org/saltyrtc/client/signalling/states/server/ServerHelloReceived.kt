package org.saltyrtc.client.signalling.states.server

import SaltyRTCClient
import org.saltyrtc.client.crypto.NaCl
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.signalling.states.BaseState
import org.saltyrtc.client.signalling.messages.outgoing.ClientAuthMessage
import org.saltyrtc.client.signalling.messages.incoming.ServerAuthMessage
import org.saltyrtc.client.signalling.messages.outgoing.ClientHelloMessage
import kotlin.reflect.KClass

/**
 * After the Initiator has received the Server hello message, a client-auth message will be sent and a server-auth message will be expected.
 *
 *  PreviousState: StartState
 *  NextState:
 */
class ServerHelloReceived(client: SaltyRTCClient) : BaseState<ServerAuthMessage>(client) {

    override suspend fun sendNextProtocolMessage() {
        var nextNonce = client.nextNonce(0) // TODO verify nonce and destination
        //As soon as the client has received the 'server-hello' message, it MUST ONLY respond with this message in case the client takes the role of a responder.
        if (client.isResponder()) {
            client.sendToWebSocket(ClientHelloMessage(nextNonce,client ).toByteArray())
            nextNonce = client.nextNonce(0)
        }
        val message = ClientAuthMessage(nextNonce, client, 0)

        /**
         * The message SHALL be NaCl public-key encrypted:
         *  by the server's session key pair (public key sent in 'server-hello')
         *  the client's permanent key pair (public key as part of the WebSocket path or sent in 'client-hello').
         */
        if (client.sessionPublicKey==null) {
            throw ValidationError("After ServerHello has been received, the session public key should not be null")
        }
        val nacl = NaCl(client.ownPermanentKey.privateKey, client.sessionPublicKey!!) //TODO perhaps create single instance in client
        client.sendToWebSocket(message.toByteArray(nacl))
    }

    override suspend fun stateActions() {
        client.role?.identity=incomingMessage.nonce.destination
    }

    override suspend fun setNextState() {
        client.state =  ResponderAuthenticatedTowardsServer(client)
    }

    override fun isAuthenticated(): Boolean {
        return false
    }

    override fun allowedMessageTypes(): Array<KClass<out ServerAuthMessage>> {
        return arrayOf(ServerAuthMessage::class)
    }


}