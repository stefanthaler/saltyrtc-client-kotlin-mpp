package org.saltyrtc.client.intents

import org.saltyrtc.client.SaltyRtcClient
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.api.requireInitiatorId
import org.saltyrtc.client.api.requireResponderId
import org.saltyrtc.client.crypto.CipherText
import org.saltyrtc.client.crypto.decrypt
import org.saltyrtc.client.entity.ClientClientAuthState
import org.saltyrtc.client.entity.ClientServerAuthState
import org.saltyrtc.client.entity.Payload
import org.saltyrtc.client.entity.messages.MessageField
import org.saltyrtc.client.entity.messages.MessageType
import org.saltyrtc.client.entity.unpack
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.protocol.*
import org.saltyrtc.client.state.ServerIdentity

internal fun SaltyRtcClient.handleMessage(it: Message) {
    logDebug("[$debugName] received message (server: ${it.isClientServer()}): $it, ")
    //TODO  handle error message and other messages

    if (it.isClientServer()) {
        handleClientServerMessage(it)
    } else {
        handleClientClientMessage(it)
    }
}

private fun Message.isClientServer(): Boolean {
    logDebug("[Message ${nonce.sequenceNumber}] ${nonce.source} => ${nonce.destination} ")
    return nonce.source == ServerIdentity
}

private fun SaltyRtcClient.handleClientServerMessage(it: Message) {
    when (current.authState) {
        ClientServerAuthState.UNAUTHENTICATED -> {
            handleServerHello(it)
        }
        ClientServerAuthState.SERVER_AUTH -> {
            handleServerAuth(it)
        }
        ClientServerAuthState.AUTHENTICATED -> {
            handleAuthenticatedMessages(it)
        }
    }
}

private fun SaltyRtcClient.handleClientClientMessage(it: Message) {
    val source = it.nonce.source
    if (current.isInitiator) {
        requireResponderId(source)
    } else {
        requireInitiatorId(source)
    }
    // TODO validate that this is a correct source
    val authState = current.clientAuthStates[source]
    requireNotNull(authState)
    when (authState) {
        ClientClientAuthState.UNAUTHENTICATED -> {
            if (current.isInitiator) {
                handleClientSessionKeyMessage(it)
                //TODO handle token message and disconnect
            } else {
                handleClientSessionKeyMessage(it)
            }
        }
        ClientClientAuthState.CLIENT_AUTH -> {
            handleClientAuthMessage(it)
        }
        ClientClientAuthState.AUTHENTICATED -> TODO()
    }
}


private fun SaltyRtcClient.handleAuthenticatedMessages(it: Message) {
    val sharedKey = current.serverSessionSharedKey
    requireNotNull(sharedKey)
    val plainText = decrypt(CipherText(it.data.bytes), it.nonce, sharedKey)
    val payloadMap = unpack(Payload(plainText.bytes))
    require(payloadMap.containsKey(MessageField.TYPE))
    val type = MessageField.type(payloadMap)
    logDebug("[$debugName] Handling authenticated message: $type")

    if (current.isInitiator) {
        when (type) {
            MessageType.NEW_RESPONDER -> handleNewResponder(payloadMap)
            MessageType.SEND_ERROR -> handleSendError(payloadMap)
            MessageType.DISCONNECTED -> handleDisconnected(payloadMap)
            else -> {
                throw IllegalArgumentException("")
            }
        }
    } else {
        when (type) {
            MessageType.NEW_INITIATOR -> handleNewInitiator()
            MessageType.DISCONNECTED -> handleDisconnected(payloadMap)
            MessageType.SEND_ERROR -> handleSendError(payloadMap)
            else -> {
                throw IllegalArgumentException("")
            }
        }
    }
}