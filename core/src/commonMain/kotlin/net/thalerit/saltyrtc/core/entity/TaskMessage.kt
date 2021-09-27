package net.thalerit.saltyrtc.core.entity

import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.api.PayloadMap
import net.thalerit.saltyrtc.api.TaskMessage
import net.thalerit.saltyrtc.core.entity.messages.type

fun taskMessage(payloadMap: PayloadMap): TaskMessage {
    return TaskMessageImpl(payloadMap)
}

private data class TaskMessageImpl(
    override val payloadMap: PayloadMap
) : TaskMessage {
    override val type: MessageType by lazy {
        MessageField.type(payloadMap)
    }
}