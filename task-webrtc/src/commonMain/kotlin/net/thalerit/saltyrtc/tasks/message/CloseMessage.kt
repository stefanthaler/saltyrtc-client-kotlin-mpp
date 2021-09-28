package net.thalerit.saltyrtc.tasks.message

import net.thalerit.saltyrtc.api.MessageType

object CloseMessage : WebRtcMessage {
    val type = MessageType.CLOSE
}