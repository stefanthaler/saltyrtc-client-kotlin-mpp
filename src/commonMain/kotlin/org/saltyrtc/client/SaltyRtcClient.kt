package org.saltyrtc.client

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.saltyrtc.client.intents.ClientIntent
import org.saltyrtc.client.state.ClientState
import org.saltyrtc.client.state.InitialClientState

class SaltyRtcClient(
    private val signallingServer: Server
) {
    private val _state = MutableStateFlow(value = InitialClientState())
    val state:SharedFlow<ClientState> = _state
    var current:ClientState
        get() = _state.value
        set(value) {
            _state.value = value
        }

    private val intents = Channel<ClientIntent>(capacity = Channel.UNLIMITED)

    private val scope = CoroutineScope(Dispatchers.Main)
    init {
        scope.launch {
            intents.receiveAsFlow().collect {
                handle(it)
            }
        }
    }

    fun queue(intent:ClientIntent) {
        intents.trySend(intent)


    }

    private fun handle(it:ClientIntent) {
        when(it) {
            is ClientIntent.Connect -> connect(it)
            else -> throw UnsupportedOperationException("Intent: $it is not supported")
        }
    }
}



fun SaltyRtcClient.connect(intent: ClientIntent) {
    // TODO
}

fun SaltyRtcClient.close() {

}
