package net.thalerit.saltyrtc.tasks.message

import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.api.TaskMessage
import net.thalerit.saltyrtc.tasks.entity.Answer
import net.thalerit.saltyrtc.tasks.entity.answer

fun answerMessage(message: TaskMessage): AnswerMessage {
    val answer = MessageField.answer(message.payloadMap)
    return AnswerMessage(answer)
}

data class AnswerMessage(
    val answer: Answer
) : WebRtcMessage {
    val type: MessageType = MessageType.ANSWER
}