package net.thalerit.saltyrtc.tasks

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.thalerit.saltyrtc.api.*

class WebRtcTask : Task<WebRtcConnection> {
    private var channel: SignallingChannel? = null

    override val url: TaskUrl by lazy {
        V1_WEBRTC_TASK
    }
    private val _connection = MutableStateFlow<Result<WebRtcConnection>?>(null)
    override val connection: StateFlow<Result<WebRtcConnection>?> = _connection

    override fun openConnection(channel: SignallingChannel) {
        this.channel = channel
    }

    override fun handleClosed(reason: CloseReason) {
        this.channel = null
    }
}