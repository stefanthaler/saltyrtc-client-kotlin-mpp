package org.saltyrtc.client.signalling.states.server

import SaltyRTCClient
import org.saltyrtc.client.crypto.NaCl
import org.saltyrtc.client.exceptions.ValidationError
import org.saltyrtc.client.extensions.toHexString
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.signalling.messages.incoming.server.authentication.ServerHelloMessage
import org.saltyrtc.client.signalling.messages.outgoing.ClientAuthMessage
import org.saltyrtc.client.signalling.messages.outgoing.ClientHelloMessage
import org.saltyrtc.client.signalling.states.BaseState
import kotlin.reflect.KClass

class ServerAuthenticationStart(client: SaltyRTCClient) : BaseState<ServerHelloMessage>(client) {

    override fun isAuthenticated(): Boolean {
        return false
    }

    override suspend fun stateActions() {
        logWarn("${getIncomingMessage().nonce.sequenceNumber}")

        client.sessionPublicKey = getIncomingMessage().key
        logWarn("Received session public key:${client.sessionPublicKey!!.toHexString()}")
        // In case this is the first message received from the sender, the peer:
        // MUST check that the overflow number of the source peer is 0 (or the upper 16 bits of the combined sequence number of the source peer are 0, in code: csn & 0xffff00000000 == 0) and,
        // if the peer has already sent a message to the sender, MUST check that the sender's cookie is different than its own cookie, and
        // MUST store cookie, overflow number and sequence number (or the combined sequence number) for checks on further messages.
        // The above number(s) SHALL be stored and updated separately for each other peer by its identity (source address in this case).
        client.server.incomingNonce=getIncomingMessage().nonce
        logWarn("Server Cookie: ${client.server.incomingNonce!!.cookie.bytes.toHexString()}")
    }

    override suspend fun sendNextProtocolMessage() {
        var nextNonce = client.server.outgoingNonce
        //As soon as the client has received the 'server-hello' message,
        // it MUST ONLY respond with this message in case the client takes the role of a responder.
        if (client.isResponder()) {
            ClientHelloMessage(nextNonce,client ).send(client)
            nextNonce = client.server.outgoingNonce
        }

        val message = ClientAuthMessage(nextNonce, client, 0)
        if (client.sessionPublicKey==null) {
            throw ValidationError("After ServerHello has been received, the session public key should not be null")
        }
        //The message SHALL be NaCl public-key encrypted by the server's session key pair (public key sent in 'server-hello')
        // and the client's permanent key pair (public key as part of the WebSocket path or sent in 'client-hello').

        val nacl = NaCl( client.ownPermanentKey.privateKey ,client.sessionPublicKey!!) //TODO perhaps create single instance in client
        message.send(client, nacl)
    }

    override suspend fun setNextState() {
        client.state= ServerHelloReceived(client)
    }

    override fun allowedMessageTypes(): Array<KClass<out ServerHelloMessage>> {
        return arrayOf(ServerHelloMessage::class)
    }
}