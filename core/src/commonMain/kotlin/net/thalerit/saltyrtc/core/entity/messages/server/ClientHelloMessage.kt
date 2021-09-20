package net.thalerit.saltyrtc.core.entity.messages.server

import net.thalerit.crypto.PublicKey
import net.thalerit.saltyrtc.api.Message
import net.thalerit.saltyrtc.api.Nonce
import net.thalerit.saltyrtc.core.entity.message
import net.thalerit.saltyrtc.core.entity.pack

@OptIn(ExperimentalStdlibApi::class)
fun clientHelloMessage(
    ownPublicKey: PublicKey,
    nonce: Nonce,
): Message {
    val payloadMap: Map<MessageField, Any> = buildMap {
        put(MessageField.TYPE, MessageType.CLIENT_HELLO.type)
        put(MessageField.KEY, ownPublicKey.bytes)
    }

    return message(
        nonce = nonce,
        data = pack(payloadMap),
    )
}