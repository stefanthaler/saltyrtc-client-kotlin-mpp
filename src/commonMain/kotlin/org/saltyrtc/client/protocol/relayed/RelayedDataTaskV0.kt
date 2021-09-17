package org.saltyrtc.client.protocol.relayed

import org.saltyrtc.client.api.Message
import org.saltyrtc.client.api.SupportedTask
import org.saltyrtc.client.api.Task
import org.saltyrtc.client.api.TaskUrl
import org.saltyrtc.client.state.ClientState

class RelayedDataTaskV0 : Task {
    override val url: TaskUrl by lazy {
        SupportedTask.V0_RELAYED_DATA.taskUrl
    }

    override fun handle(it: Message, state: ClientState): ClientState {
        TODO("Not yet implemented")
    }

    override fun send(it: Message, state: ClientState): ClientState {
        TODO("Not yet implemented")
    }
}