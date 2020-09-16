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
        // In case this is the first message received from the sender, the peer:
        // MUST check that the overflow number of the source peer is 0 (or the upper 16 bits of the combined sequence number of the source peer are 0, in code: csn & 0xffff00000000 == 0) and,
        // if the peer has already sent a message to the sender, MUST check that the sender's cookie is different than its own cookie, and
        // MUST store cookie, overflow number and sequence number (or the combined sequence number) for checks on further messages.
        // The above number(s) SHALL be stored and updated separately for each other peer by its identity (source address in this case).
        client.server.nonce=getIncomingMessage().nonce
        client.server.theirCookie=getIncomingMessage().nonce.cookie
    }

    override suspend fun setNextState() {
        client.state= ServerHelloReceived(client)
    }

    override fun allowedMessageTypes(): Array<KClass<out ServerHelloMessage>> {
        return arrayOf(ServerHelloMessage::class)
    }
}