package net.thalerit.saltyrtc.tasks.message

import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.api.TaskMessage
import net.thalerit.saltyrtc.tasks.entity.Offer
import net.thalerit.saltyrtc.tasks.entity.offer

fun offerMessage(message: TaskMessage): OfferMessage {
    val offer = MessageField.offer(message.payloadMap)
    return OfferMessage(offer)
}

data class OfferMessage(
    val offer: Offer
) : WebRtcMessage {
    val type: MessageType = MessageType.OFFER
}