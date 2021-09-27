package net.thalerit.saltyrtc.core.intents

import kotlinx.coroutines.flow.MutableSharedFlow
import net.thalerit.crypto.CipherText
import net.thalerit.crypto.SharedKey
import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.ClientClientAuthState
import net.thalerit.saltyrtc.core.entity.ClientServerAuthState
import net.thalerit.saltyrtc.core.entity.messages.type
import net.thalerit.saltyrtc.core.logging.logDebug
import net.thalerit.saltyrtc.core.protocol.*
import net.thalerit.saltyrtc.core.state.ServerIdentity
import net.thalerit.saltyrtc.core.util.requireInitiatorId
import net.thalerit.saltyrtc.core.util.requireResponderId
import net.thalerit.saltyrtc.crypto.decrypt

internal fun SaltyRtcClient.handleMessage(it: Message) {
    val nonce = it.nonce
    val isClient2ServerMessage = it.isClientServer()
    val type = if (isClient2ServerMessage) "Client2Server" else "Client2Client"
    logDebug("[$debugName] received $type message ${nonce.sequenceNumber}] ${nonce.source} => ${nonce.destination}  ")
    //TODO  handle error message and other messages
    if (isClient2ServerMessage) {
        handleClientServerMessage(it)
    } else {
        handleClientClientMessage(it)
    }
}

private fun Message.isClientServer(): Boolean {
    logDebug("[Message] ${nonce.sequenceNumber}] ${nonce.source} => ${nonce.destination} ")
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
    requireNotNull(authState) { "[$debugName] AuthState $source: ${current.clientAuthStates} " }
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
        ClientClientAuthState.AUTHENTICATED -> {
            handleAuthenticatedClientClientMessage(it)
        }
    }
}

private fun SaltyRtcClient.handleAuthenticatedClientClientMessage(it: Message) {
    val source = it.nonce.source
    val sessionSharedKey = current.sessionSharedKeys[source]
    requireNotNull(sessionSharedKey)
    when (messageType(it, sessionSharedKey, msgPacker)) {
        MessageType.APPLICATION -> handleApplicationMessage(it)
        MessageType.CLOSE -> handleClose(it)
        else -> {
            // TODO checks
            val incoming = current.signallingChannel!!.message as MutableSharedFlow
            val plainText = decrypt(CipherText(it.data.bytes), it.nonce, sessionSharedKey)
            val unpacked = msgPacker.unpack(Payload(plainText.bytes))
            incoming.tryEmit(
                unpacked
            )
        }
    }
}

private fun messageType(it: Message, sharedKey: SharedKey, msgPacker: MessagePacker): MessageType {
    val plainText = decrypt(CipherText(it.data.bytes), it.nonce, sharedKey)
    val payloadMap = msgPacker.unpack(Payload(plainText.bytes))
    return MessageField.type(payloadMap)
}

private fun SaltyRtcClient.handleAuthenticatedMessages(it: Message) {
    val sharedKey = current.serverSessionSharedKey
    requireNotNull(sharedKey)
    val plainText = decrypt(CipherText(it.data.bytes), it.nonce, sharedKey)
    val payloadMap = msgPacker.unpack(Payload(plainText.bytes))
    require(payloadMap.containsKey(MessageField.TYPE))
    val type = MessageField.type(payloadMap)
    logDebug("[$debugName] Authenticated Client2Server message: $type")

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