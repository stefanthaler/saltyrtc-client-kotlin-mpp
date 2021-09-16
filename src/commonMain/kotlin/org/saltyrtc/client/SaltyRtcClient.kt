package org.saltyrtc.client

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.crypto.NaClKeyPair
import org.saltyrtc.client.entity.ClientServerAuthState
import org.saltyrtc.client.entity.messages.serverHelloMessage
import org.saltyrtc.client.intents.ClientIntent
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.state.ClientState
import org.saltyrtc.client.state.initialClientState

class SaltyRtcClient(
    internal val signallingServer: Server,
    override val ownPermanentKey: NaClKeyPair
) : Client {
    private val _state = MutableStateFlow(value = initialClientState())
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

    private fun handle(it: ClientIntent) {
        when (it) {
            is ClientIntent.Connect -> connect(it)
            else -> throw UnsupportedOperationException("Intent: $it is not supported")
        }
    }

    fun queue(intent: ClientIntent) {
        intents.trySend(intent)
    }

    internal val messageSupervisor = SupervisorJob()
    internal val messageScope = CoroutineScope(Dispatchers.Default + messageSupervisor)

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
    return nonce.destination == 0.toByte()
}

private fun SaltyRtcClient.handleMessage(it: Message) {
    logDebug("[SaltyRtcClient] received message: $it")
    if (it.isClientServer()) {
        handleClientServerMessage(it)
    } else {
        handleClientClientMessage(it)
    }
}

private fun SaltyRtcClient.handleClientServerMessage(it: Message) {
    when (current.authState) {
        ClientServerAuthState.DISCONNECTED -> {
            handleServerHello(it)
        }
        ClientServerAuthState.SERVER_AUTH -> TODO()
        ClientServerAuthState.CONNECTED -> TODO()
    }
}

/**
 * This message MUST be sent by the server after a client connected to the server using a valid signalling path.
 * The server MUST generate a new cryptographically secure random NaCl key pair for each client.
 * The public key (32 bytes) of that key pair MUST be set in the key field of this message.
 *
 * A receiving client MUST check that the message contains a valid NaCl public key (the size of the key MUST be
 * exactly 32 bytes). In case the client has knowledge of the server's public permanent key, it SHALL ensure that the
 * server's public session key is different to the server's public permanent key.
 *
 * The message SHALL NOT be encrypted.
 */
private fun SaltyRtcClient.handleServerHello(it: Message) {
    val message = serverHelloMessage(it)
    require(message.key != signallingServer.permanentPublicKey)
    current = current.copy(
        authState = ClientServerAuthState.SERVER_AUTH,
        sessionKey = message.key
    )

    sendClientHello()
}

private fun SaltyRtcClient.sendClientHello() {

}

private fun SaltyRtcClient.handleClientClientMessage(it: Message) {

}


private fun SaltyRtcClient.connect(intent: ClientIntent.Connect) {
    messageSupervisor.cancelChildren()
    current.socket?.close()

    val socket = webSocket(signallingServer)
    messageScope.launch {
        socket.message.collect {
            handleMessage(it)
        }
    }

    current = current.copy(
        socket = socket,
        isInitiator = intent.isInitiator,
        authState = ClientServerAuthState.DISCONNECTED
    )

    socket.open(intent.path)
}

fun SaltyRtcClient.close() {
    val socket = current.socket
    if (socket == null) {
        logWarn("Attempted to close an uninitialized socket")
    } else {
        socket.close()
    }
}
