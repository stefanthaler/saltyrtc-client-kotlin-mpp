package net.thalerit.saltyrtc.tasks.protocol

import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.tasks.WebRtcIntent
import net.thalerit.saltyrtc.tasks.entity.Offer

fun SaltyRtcClient.sendOffer(offer: Offer) {
    queue(WebRtcIntent.SendOffer(offer))
}