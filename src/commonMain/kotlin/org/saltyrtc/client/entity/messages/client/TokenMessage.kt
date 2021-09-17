package org.saltyrtc.client.entity.messages.client

import org.saltyrtc.client.api.Message
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.crypto.SharedKey
import org.saltyrtc.client.entity.Payload
import org.saltyrtc.client.entity.message
import org.saltyrtc.client.entity.messages.MessageField
import org.saltyrtc.client.entity.messages.MessageType
import org.saltyrtc.client.entity.nonce
import org.saltyrtc.client.entity.pack
import org.saltyrtc.client.state.Identity
import org.saltyrtc.client.state.InitiatorIdentity

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