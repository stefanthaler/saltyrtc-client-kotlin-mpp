package net.thalerit.saltyrtc.tasks

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.tasks.entity.SecureDataChannel

/**
 * This task uses the end-to-end encryption techniques of SaltyRTC to set up a secure WebRTC peer-to-peer connection.
 * It also adds another security layer for data channels that are available to users.
 *
 * The signalling channel will persist and should be handed over to a dedicated data channel once the peer-to-peer
 * connection has been set up. Therefore, further signalling communication between the peers may not require a
 * dedicated WebSocket connection over a SaltyRTC server.
 */
class WebRtcTask(
    private val exclude: List<Int>,
    private val isHandover: Boolean,
) : Task {
    private var channel: SignallingChannel? = null
    private var secureDataChannel: SecureDataChannel? = null

    private var _isInitialized: Boolean?
        get() = isInitialized.value
        set(value) {
            isInitialized as MutableStateFlow<Boolean?>
            isInitialized.value = value
        }
    override val isInitialized: StateFlow<Boolean?> = MutableStateFlow(null)

    init {
        exclude.requireValidChannelIds()
    }

    private val _state = MutableStateFlow(value = initialState())
    internal val state: SharedFlow<State> = _state
    internal var current: State
        get() = _state.value
        set(value) {
            _state.value = value
        }

    internal var negotiatedHandover: Boolean = false
    lateinit var otherExclude: List<Int>

    override val url: TaskUrl by lazy {
        V1_WEBRTC_TASK
    }

    override fun openConnection(channel: SignallingChannel, data: Any?) {
        this.channel = channel

        val dataMap = data as Map<String, Any> // TODO proper validation
        otherExclude = dataMap[paramExclude] as List<Int>
        negotiatedHandover = dataMap[paramExclude] as Boolean && isHandover
        otherExclude.requireValidChannelIds()

        _isInitialized = true
    }

    override fun handleClosed(reason: CloseReason) {
        this.channel = null // TODO
    }

    internal fun assemble(chunk: ByteArray): TaskMessage? {
        //TODO
        // collect chunks
        // if message is complete, assemble and clear chunks.
        // if first message, set their cookie. validate nonce otherwise
        // decrypt

        // should end up at webRtcMessage
        return null
    }

    // should not be called directly
    override fun handle(intent: TaskIntent) { // outgoing tasks
        // TODO handle if is handover was already performed
        require(
            intent.type in listOf(
                MessageType.HANDOVER,
                MessageType.OFFER,
                MessageType.CANDIDATES,
                MessageType.ANSWER
            )
        )

        if (intent is WebRtcIntent.SendHandover) {
            secureDataChannel = intent.secureDataChannel
            current = current.copy(
                handoverSent = true
            )
            // Todo
            //  initialize nonce
        }

        if (current.handoverIsCompleted) {
            val data: ByteArray = TODO("Convert intent to secure, chunked data message")
            //TODO
            // encrypt
            // chunk
            // increase nonce and stuff

            secureDataChannel?.send?.invoke(data)
        } else {
            val signallingChannel = channel!!
            signallingChannel.send(intent.payloadMap)
        }
    }

    override fun emitToClient(taskMessage: TaskMessage): Boolean { // incoming task messages
        return when (taskMessage.type) {
            MessageType.OFFER -> true
            MessageType.ANSWER -> true
            MessageType.CANDIDATES -> true
            MessageType.HANDOVER -> {
                current = current.copy(
                    handoverReceived = true
                )
                false
            }
            else -> throw IllegalArgumentException("TaskMessage is not supported")
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    override val authData: Any by lazy {
        buildMap<String, Any> {
            put(paramExclude, exclude)
            put(paramHandover, isHandover)
        }
    }

    private companion object {
        const val paramExclude: String = "exclude"
        const val paramHandover: String = "handover"
    }
}

private fun List<Int>.requireValidChannelIds() {
    forEach {
        require(it in 0..65535)
    }
}

internal val SaltyRtcClient.webRtcTask: WebRtcTask
    get() = current.task!! as WebRtcTask


