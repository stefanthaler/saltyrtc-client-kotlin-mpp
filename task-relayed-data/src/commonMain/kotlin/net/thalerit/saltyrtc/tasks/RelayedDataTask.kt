package net.thalerit.saltyrtc.tasks

import net.thalerit.saltyrtc.api.*

class RelayedDataTaskV0 : Task {
    private var channel: SignallingChannel? = null

    override val url: TaskUrl by lazy {
        V0_RELAYED_DATA
    }

    override val authData: Any? = null

    override fun openConnection(newChannel: SignallingChannel, data: Any?) {
        channel = newChannel
        require(data == null)
    }

    override fun handleClosed(reason: CloseReason) {
        // TODO
    }

    private fun handleMessage(it: TaskMessage) {

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



