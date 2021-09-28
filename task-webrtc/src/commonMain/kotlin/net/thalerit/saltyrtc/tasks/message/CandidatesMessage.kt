package net.thalerit.saltyrtc.tasks.message

import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.api.TaskMessage
import net.thalerit.saltyrtc.tasks.entity.Candidate
import net.thalerit.saltyrtc.tasks.entity.candidates

fun candidatesMessage(message: TaskMessage): CandidatesMessage {
    return CandidatesMessage(
        candidates = MessageField.candidates(message.payloadMap)
    )
}

data class CandidatesMessage(
    val candidates: List<Candidate?>
) : WebRtcMessage {
    val type = MessageType.CANDIDATES
}