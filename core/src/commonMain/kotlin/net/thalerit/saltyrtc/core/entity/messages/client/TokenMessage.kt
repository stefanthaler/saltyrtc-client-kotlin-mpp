package net.thalerit.saltyrtc.core.entity.messages.client

import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.message
import net.thalerit.saltyrtc.core.entity.nonce
import net.thalerit.saltyrtc.core.state.InitiatorIdentity
import net.thalerit.saltyrtc.crypto.PublicKey
import net.thalerit.saltyrtc.crypto.SharedKey

@OptIn(ExperimentalStdlibApi::class)
fun SaltyRtcClient.oneTimeTokenMessage(
    destination: Identity,
    oneTimeToken: SharedKey
): Message {
    val nonce = nonce(InitiatorIdentity, destination)

    val payloadMap: Map<MessageField, Any> = buildMap {
        put(MessageField.TYPE, MessageType.TOKEN.type)
        put(MessageField.KEY, oneTimeToken)
    }

    return message(
        nonce = nonce,
        data = Payload(pack(payloadMap).bytes),
    )
}

data class TokenMessage(
    val key: PublicKey,
)