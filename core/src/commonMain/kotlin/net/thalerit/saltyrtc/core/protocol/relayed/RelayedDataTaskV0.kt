package net.thalerit.saltyrtc.core.protocol.relayed

import net.thalerit.saltyrtc.api.SupportedTask
import net.thalerit.saltyrtc.api.Task
import net.thalerit.saltyrtc.api.TaskUrl


class RelayedDataTaskV0 : Task {
    override val url: TaskUrl by lazy {
        SupportedTask.V0_RELAYED_DATA.taskUrl
    }

//    override fun handle(it: Message, state: ClientState): ClientState {
//        handleDataMessage(it, state)
//        return state
//    }
//
//    override fun send(it: Message, state: ClientState): ClientState {
//        TODO()
//    }
}