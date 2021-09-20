package net.thalerit.saltyrtc.core.entity.messages.client

import net.thalerit.crypto.PublicKey
import net.thalerit.crypto.SharedKey
import net.thalerit.saltyrtc.api.Identity
import net.thalerit.saltyrtc.api.Message
import net.thalerit.saltyrtc.api.Payload
import net.thalerit.saltyrtc.core.entity.message
import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.core.entity.nonce
import net.thalerit.saltyrtc.core.entity.pack
import net.thalerit.saltyrtc.core.state.InitiatorIdentity

@OptIn(ExperimentalStdlibApi::class)
fun oneTimeTokenMessage(
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