package net.thalerit.saltyrtc.tasks.message


import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.api.TaskMessage
import net.thalerit.saltyrtc.core.entity.messages.data

fun applicationMessage(message: TaskMessage): ApplicationMessage {
    return ApplicationMessage(MessageField.data(message.payloadMap))
}

data class ApplicationMessage(
    val data: Any
) : WebRtcMessage {
    val type = MessageType.APPLICATION
}