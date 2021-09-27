package net.thalerit.saltyrtc.core.entity

import net.thalerit.saltyrtc.api.Identity
import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.SignallingChannel
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.crypto.PlainText

fun signallingChannel(
    destination: Identity, client: SaltyRtcClient
): SignallingChannel {
    return SignallingChannelImpl(destination, client)
}

private class SignallingChannelImpl(
    private var destination: Identity,
    private val saltyRtcClient: SaltyRtcClient
) : SignallingChannel {
    override fun send(payloadMap: Map<MessageField, Any>) {
        saltyRtcClient.send(destination, PlainText(saltyRtcClient.pack(payloadMap).bytes))
    }
}