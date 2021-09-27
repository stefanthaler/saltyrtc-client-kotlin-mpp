package net.thalerit.saltyrtc.tasks.intent

import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.api.TaskIntent

sealed class WebRtcIntent : TaskIntent {
    data class SendOffer(
        override val type: MessageType
    ) : WebRtcIntent()

    data class SendAnswer(
        override val type: MessageType
    ) : WebRtcIntent()

    data class SendCandidates(
        override val type: MessageType
    ) : WebRtcIntent()

}