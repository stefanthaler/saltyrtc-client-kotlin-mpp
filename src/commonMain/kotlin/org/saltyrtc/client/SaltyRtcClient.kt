package org.saltyrtc.client

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import org.saltyrtc.client.crypto.NaClKeyPair
import org.saltyrtc.client.intents.ClientIntent
import org.saltyrtc.client.logging.logDebug
import org.saltyrtc.client.state.ClientState

import org.saltyrtc.client.state.initialClientState

class SaltyRtcClient(
    internal val signallingServer: Server,
    override val ownPermanentKey: NaClKeyPair
):Client {
    private val _state = MutableStateFlow(value = initialClientState())
    val state:SharedFlow<ClientState> = _state
    var current:ClientState
        get() = _state.value
        set(value) {
            _state.value = value
        }

    private val intents = Channel<ClientIntent>(capacity = Channel.UNLIMITED)
    private val intentScope = CoroutineScope(Dispatchers.Main) // TODO
    init {
        intentScope.launch {
            intents.receiveAsFlow().collect {
                handle(it)
            }
        }
    }
    private fun handle(it:ClientIntent) {
        when(it) {
            is ClientIntent.Connect -> connect(it)
            else -> throw UnsupportedOperationException("Intent: $it is not supported")
        }
    }
    fun queue(intent:ClientIntent) {
        intents.trySend(intent)
    }
    internal val messageSupervisor = SupervisorJob()
    internal val messageScope = CoroutineScope(Dispatchers.Main + messageSupervisor)

    internal fun handle(it:Message) {
        logDebug("[SaltyRtcClient] received message: $it")
    }



}


private fun SaltyRtcClient.connect(intent: ClientIntent.Connect) {
    messageSupervisor.cancelChildren()
    current.socket?.close()

    val socket = webSocket(signallingServer)
    messageScope.launch {
        socket.message.collect {
            handle(it)
        }
    }

    current = current.copy(
        socket = socket,
        isInitiator = intent.isInitiator
    )

    socket.open(intent.path)
}

fun SaltyRtcClient.close() {

}
