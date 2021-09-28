package net.thalerit.saltyrtc.tasks

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.thalerit.saltyrtc.api.*

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

    private val intents = Channel<WebRtcIntent>(capacity = Channel.UNLIMITED)
    internal val intentScope = CoroutineScope(Dispatchers.Default) // TODO

    init {
        intentScope.launch {
            intents.receiveAsFlow().collect {
                handle(it)
            }
        }
    }

    private suspend fun handle(it: WebRtcIntent) {
        when (it) {
            // TODO
        }
    }

    fun queue(intent: WebRtcIntent) {
        intents.trySend(intent)
    }

    internal var negotiatedHandover: Boolean = false
    lateinit var otherExclude: List<Int>

    override val url: TaskUrl by lazy {
        V1_WEBRTC_TASK
    }

    override fun openConnection(channel: SignallingChannel, data: Any?) {
        this.channel = channel

        val dataMap = data as Map<String, Any>
        otherExclude = dataMap[paramExclude] as List<Int>
        negotiatedHandover = dataMap[paramExclude] as Boolean && isHandover
        otherExclude.requireValidChannelIds()
    }


    override fun handleClosed(reason: CloseReason) {
        this.channel = null
    }

    override fun handle(intent: TaskIntent) {
        TODO("Not yet implemented")
    }

    override fun emitToClient(taskMessage: TaskMessage): Boolean {
        TODO("Not yet implemented")
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