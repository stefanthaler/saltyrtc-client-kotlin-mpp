package net.thalerit.saltyrtc.core.entity

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import net.thalerit.crypto.PlainText
import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.Nonce
import net.thalerit.saltyrtc.api.PayloadMap
import net.thalerit.saltyrtc.api.SignallingChannel
import net.thalerit.saltyrtc.core.SaltyRtcClient

fun signallingChannel(nonce: Nonce, client: SaltyRtcClient): SignallingChannel {
    return SignallingChannel(nonce, client)
}

private class SignallingChannel(
    private var nonce: Nonce,
    private val saltyRtcClient: SaltyRtcClient
) : SignallingChannel {

    override fun send(payloadMap: Map<MessageField, Any>) {
        nonce = nonce.withIncreasedSequenceNumber()
        val unencrypted = unencryptedMessage(
            nonce = nonce,
            plainText = PlainText(pack(payloadMap).bytes)
        )
        saltyRtcClient.send(unencrypted)
    }

    override val message: SharedFlow<PayloadMap> = MutableSharedFlow(replay = 1)
}