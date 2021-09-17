package org.saltyrtc.client

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import org.saltyrtc.client.api.Client
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.api.Server
import org.saltyrtc.client.api.SignallingPath
import org.saltyrtc.client.crypto.NaClKeyPair
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.entity.Task
import org.saltyrtc.client.intents.ClientIntent
import org.saltyrtc.client.intents.connect
import org.saltyrtc.client.intents.handleMessage
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.logging.logWarn
import org.saltyrtc.client.state.ClientState
import org.saltyrtc.client.state.Identity
import org.saltyrtc.client.state.initialClientState

class SaltyRtcClient(
    val debugName: String = "SaltyRtcClient",
    internal val signallingServer: Server,
    override val ownPermanentKey: NaClKeyPair,
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

    override fun connect(
        isInitiator: Boolean,
        path: SignallingPath,
        task: Task,
        otherPermanentPublicKey: PublicKey?
    ) {
        queue(
            ClientIntent.Connect(
                isInitiator = isInitiator,
                path = path,
                task = task,
                otherPermanentPublicKey = otherPermanentPublicKey
            )
        )
    }
}


internal fun SaltyRtcClient.clearInitiatorPath() {
    //TODO
}

internal fun SaltyRtcClient.clearResponderPath(identity: Identity) {
    current = current.copy(
        receivingNonces = current.receivingNonces.toMutableMap().apply {
            remove(identity)
        }
        // TODO clear responders
    )
}

fun SaltyRtcClient.close() {
    val socket = current.socket
    if (socket == null) {
        logWarn("[$debugName] Attempted to close an uninitialized socket")
    } else {
        socket.close()
    }
}
