package org.saltyrtc.client.entity.messages.server

import org.saltyrtc.client.api.Nonce
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.entity.message
import org.saltyrtc.client.entity.pack

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