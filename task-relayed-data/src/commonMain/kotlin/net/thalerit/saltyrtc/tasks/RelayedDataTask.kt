package net.thalerit.saltyrtc.tasks

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.thalerit.saltyrtc.api.*

class RelayedDataTaskV0 : Task<RelayedDataConnection> {
    private var channel: SignallingChannel? = null

    override val url: TaskUrl by lazy {
        V0_RELAYED_DATA
    }

    override val connection: StateFlow<Result<RelayedDataConnection>?> = MutableStateFlow(null)

    override fun openConnection(newChannel: SignallingChannel) {
        channel = newChannel
        (connection as MutableStateFlow).value = Result.success(RelayedDataConnection(newChannel))
    }

    override fun handleClosed(reason: CloseReason) {
        (connection as MutableStateFlow).value = null // TODO handle
    }
}