package net.thalerit.saltyrtc.core.entity

import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.api.PayloadMap
import net.thalerit.saltyrtc.api.TaskIntent
import net.thalerit.saltyrtc.core.entity.messages.type

fun taskIntent(payloadMap: PayloadMap): TaskIntent {
    return TaskIntentImpl(payloadMap)
}

private data class TaskIntentImpl(
    val payloadMap: PayloadMap
) : TaskIntent {
    override val type: MessageType by lazy {
        MessageField.type(payloadMap)
    }
}