package net.thalerit.saltyrtc.tasks

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.thalerit.saltyrtc.api.Connection
import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.SignallingChannel
import net.thalerit.saltyrtc.tasks.message.dataMessage

class RelayedDataConnection(
    private val channel: SignallingChannel
) : Connection {
    val data: Flow<Any> = channel.message.map { it[MessageField.P]!! } // TODO error handling

    fun send(data: Any) {
        channel.send(dataMessage(data))
    }
}