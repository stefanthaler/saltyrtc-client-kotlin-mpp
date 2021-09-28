package net.thalerit.saltyrtc.core

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.entity.ClientServerAuthState
import net.thalerit.saltyrtc.core.entity.message
import net.thalerit.saltyrtc.core.intents.ClientIntent
import net.thalerit.saltyrtc.core.intents.connect
import net.thalerit.saltyrtc.core.intents.handleMessage
import net.thalerit.saltyrtc.core.state.ClientState
import net.thalerit.saltyrtc.core.state.initialClientState
import net.thalerit.saltyrtc.core.state.nextSendingNonce
import net.thalerit.saltyrtc.core.state.withSendingNonce
import net.thalerit.saltyrtc.crypto.NaClKeyPair
import net.thalerit.saltyrtc.crypto.PlainText
import net.thalerit.saltyrtc.crypto.PublicKey
import net.thalerit.saltyrtc.crypto.encrypt
import net.thalerit.saltyrtc.logging.Loggable
import net.thalerit.saltyrtc.logging.Logger
import net.thalerit.saltyrtc.logging.d
import net.thalerit.saltyrtc.logging.w

// TODO builder
class SaltyRtcClient(
    val debugName: String = "SaltyRtcClient",
    internal val signallingServer: Server,
    override val ownPermanentKey: NaClKeyPair,
    private val msgPacker: MessagePacker,
    override val logger: Logger
) : Client, Loggable {

    private val _state = MutableStateFlow(value = initialClientState())
    val state: SharedFlow<ClientState> = _state
    var current: ClientState // TODO public client state
        get() = _state.value
        set(value) {
            _state.value = value
        }

    private val intents = Channel<ClientIntent>(capacity = Channel.UNLIMITED)
    internal val intentScope = CoroutineScope(Dispatchers.Default) // TODO

    init {
        intentScope.launch {
            intents.receiveAsFlow().collect {
                handle(it)
            }
        }
    }

    private suspend fun handle(it: ClientIntent) {
        d("[$debugName] handling intent $it")
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
            w("[$debugName] Attempted to send message to uninitialized socket")
        } else {
            socket.send(message)
            current = current.withSendingNonce(message.nonce)
        }
    }

    internal fun send(destination: Identity, plaintext: PlainText) {
        // TODO checks
        val nonce = current.nextSendingNonce(destination)
        val sharedSessionKey = current.sessionSharedKeys[destination]!!
        val encryptedPayload = Payload(encrypt(Payload(plaintext.bytes), nonce, sharedSessionKey).bytes)
        val message = message(nonce, encryptedPayload)
        queue(ClientIntent.SendMessage(message))
    }

    override suspend fun connect(
        isInitiator: Boolean,
        path: SignallingPath,
        task: Task,
        webSocket: (Server) -> WebSocket,
        otherPermanentPublicKey: PublicKey?
    ): Result<Unit> {
        val result: Result<Unit> = suspendCancellableCoroutine { continuation ->
            queue(
                ClientIntent.Connect(
                    isInitiator = isInitiator,
                    path = path,
                    task = task,
                    webSocket = webSocket,
                    continuation = continuation,
                    otherPermanentPublicKey = otherPermanentPublicKey
                )
            )
            // will suspend here until continuation is resumed
        }

        result.onSuccess {
            return Result.success(Unit) // TODO manage unsafe cast
        }
        result.onFailure {
            return Result.failure(it)
        }
        throw IllegalStateException()
    }

    /**
     * Pass a TaskIntent to the initialized task.
     */
    override fun send(intent: TaskIntent) {
        require(current.authState == ClientServerAuthState.AUTHENTICATED)
        current.task!!
        taskIntents.trySend(intent)
    }

    private val taskIntents = Channel<TaskIntent>(Channel.UNLIMITED)

    init {
        // TODO more fine grained management
        intentScope.launch {
            taskIntents.receiveAsFlow().collect {
                val task = current.task!!
                task.handle(it)
            }
        }
    }

    private val taskMessages = Channel<TaskMessage>(Channel.UNLIMITED)
    override val message: Flow<TaskMessage> = taskMessages.receiveAsFlow()
    internal fun emit(taskMessage: TaskMessage) {
        taskMessages.trySend(taskMessage)
    }

    internal fun unpack(payload: Payload): Map<MessageField, Any> = msgPacker.unpack(payload)

    internal fun pack(payloadMap: Map<MessageField, Any>): Payload = msgPacker.pack(payloadMap)
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
        w("[$debugName] Attempted to close an uninitialized socket")
    } else {
        socket.close()
    }
}