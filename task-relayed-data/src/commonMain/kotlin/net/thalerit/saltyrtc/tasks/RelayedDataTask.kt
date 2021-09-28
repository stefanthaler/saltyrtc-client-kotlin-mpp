package net.thalerit.saltyrtc.tasks

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.thalerit.saltyrtc.api.*

class RelayedDataTaskV0 : Task {
    private var channel: SignallingChannel? = null

    override val isInitialized: StateFlow<Boolean?> = MutableStateFlow(null)

    override val url: TaskUrl by lazy {
        V0_RELAYED_DATA
    }

    override val authData: Any? = null

    override fun openConnection(newChannel: SignallingChannel, data: Any?) {
        channel = newChannel
        require(data == null)
        (isInitialized as MutableStateFlow<Boolean?>).value = true
    }

    override fun handleClosed(reason: CloseReason) {
        // TODO
    }

    override fun handle(intent: TaskIntent) {
        val outChannel = channel!!
        require(intent.type in listOf(MessageType.DATA, MessageType.APPLICATION))
        outChannel.send(intent.payloadMap)
    }

    override fun emitToClient(taskMessage: TaskMessage): Boolean {
        require(taskMessage.type in listOf(MessageType.DATA, MessageType.APPLICATION))
        return true
    }
}



