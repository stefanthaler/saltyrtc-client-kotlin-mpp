package net.thalerit.saltyrtc.core.entity

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import net.thalerit.saltyrtc.api.Identity
import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.PayloadMap
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

        saltyRtcClient.send(destination, PlainText(saltyRtcClient.msgPacker.pack(payloadMap).bytes))
    }

    override val message: SharedFlow<PayloadMap> = MutableSharedFlow(replay = 1)
}