package net.thalerit.saltyrtc.tasks.protocol

import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.tasks.WebRtcIntent
import net.thalerit.saltyrtc.tasks.entity.Answer

fun SaltyRtcClient.sendAnswer(answer: Answer) {
    queue(WebRtcIntent.SendAnswer(answer))
}