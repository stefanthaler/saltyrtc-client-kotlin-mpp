package net.thalerit.saltyrtc.tasks.protocol

import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.tasks.WebRtcIntent
import net.thalerit.saltyrtc.tasks.entity.Candidate

fun SaltyRtcClient.sendCandidates(candidates: List<Candidate>) {
    queue(WebRtcIntent.SendCandidates(candidates))
}