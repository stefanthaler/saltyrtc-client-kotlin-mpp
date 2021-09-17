package org.saltyrtc.client

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.crypto.CipherText
import org.saltyrtc.client.crypto.NaClKeyPair
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.crypto.decrypt
import org.saltyrtc.client.entity.ClientServerAuthState
import org.saltyrtc.client.entity.Payload
import org.saltyrtc.client.entity.messages.MessageField
import org.saltyrtc.client.entity.messages.MessageType
import org.saltyrtc.client.entity.unpack
import org.saltyrtc.client.intents.ClientIntent
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.protocol.*
import org.saltyrtc.client.state.ClientState
import org.saltyrtc.client.state.Identity
import org.saltyrtc.client.state.ServerIdentity
import org.saltyrtc.client.state.initialClientState

class SaltyRtcClient(
    val debugName: String = "SaltyRtcClient",
    internal val signallingServer: Server,
    override val ownPermanentKey: NaClKeyPair,
    otherPermanentPublicKey: PublicKey?
) : Client {
    private val _state = MutableStateFlow(value = initialClientState(otherPermanentPublicKey))
    val state: SharedFlow<ClientState> = _state
    var current: ClientState
        get() = _state.value
        set(value) {
            _state.value = value
        }

    private val intents = Channel<ClientIntent>(capacity = Channel.UNLIMITED)
    private val intentScope = CoroutineScope(Dispatchers.Default) // TODO

    init {
        intentScope.launch {
            intents.receiveAsFlow().collect {
                handle(it)
            }
        }
    }

    private suspend fun handle(it: ClientIntent) {
        logDebug("[$debugName] handling intent $it")
        when (it) {
            is ClientIntent.Connect -> connect(it)
            is ClientIntent.MessageReceived -> handleMessage(it.message)
            is ClientIntent.SendMessage -> send(it.message)
        }
    }

    fun queue(intent: ClientIntent) {
        intents.trySend(intent)
    }

    internal val messageSupervisor = SupervisorJob()
    internal val messageScope = CoroutineScope(Dispatchers.Default + messageSupervisor)

    private suspend fun send(message: Message) = withContext(messageScope.coroutineContext) {
        val socket = current.socket
        if (socket == null) {
            logWarn("[$debugName] Attempted to send message to unitialized socket")
        } else {
            socket.send(message)
        }
    }

    override fun connect(isInitiator: Boolean, path: SignallingPath) {
        queue(
            ClientIntent.Connect(
                isInitiator = isInitiator,
                path = path,
            )
        )
    }
}

private fun Message.isClientServer(): Boolean {
    logDebug("[Message ${nonce.sequenceNumber}] ${nonce.source} => ${nonce.destination} ")
    return nonce.source == ServerIdentity
}

private fun SaltyRtcClient.handleMessage(it: Message) {
    logDebug("[$debugName] received message (server: ${it.isClientServer()}): $it, ")
    //TODO  handle error message and other messages

    if (it.isClientServer()) {
        handleClientServerMessage(it)
    } else {
        handleClientClientMessage(it)
    }
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


internal fun SaltyRtcClient.clearInitiatorPath() {
    //TODO
}

internal fun SaltyRtcClient.clearResponderPath(identity: Identity) {
    current = current.copy(
        nonces = current.nonces.toMutableMap().apply {
            remove(identity)
        }
        // TODO clear responders
    )
}


private fun SaltyRtcClient.handleClientClientMessage(it: Message) {

}

private fun SaltyRtcClient.connect(intent: ClientIntent.Connect) {
    messageSupervisor.cancelChildren()
    current.socket?.close()

    val socket = webSocket(signallingServer)
    messageScope.launch {
        socket.message.collect {
            queue(ClientIntent.MessageReceived(it))
        }
    }

    current = current.copy(
        socket = socket,
        isInitiator = intent.isInitiator,
        authState = ClientServerAuthState.UNAUTHENTICATED
    )

    socket.open(intent.path)
}

fun SaltyRtcClient.close() {
    val socket = current.socket
    if (socket == null) {
        logWarn("[$debugName] Attempted to close an uninitialized socket")
    } else {
        socket.close()
    }
}
