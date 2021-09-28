package net.thalerit.saltyrtc.tasks.message

import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.api.TaskMessage

sealed interface WebRtcMessage

fun TaskMessage.toWebRtcMessage(): WebRtcMessage {
    return when (type) {
        MessageType.APPLICATION -> applicationMessage(this)
        MessageType.OFFER -> offerMessage(this)
        MessageType.ANSWER -> answerMessage(this)
        MessageType.CANDIDATES -> candidatesMessage(this)
        MessageType.CLOSE -> CloseMessage
        else -> {
            throw IllegalArgumentException("Illegal message type: $type")
        }
    }
}