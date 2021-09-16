package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.Nonce
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.entity.Payload
import org.saltyrtc.client.entity.pack

@OptIn(ExperimentalStdlibApi::class)
fun clientHelloMessage(
    ownPublicKey: PublicKey,
    nonce: Nonce,
): ClientHelloMessage {
    val payloadMap: Map<MessageField, Any> = buildMap {
        put(MessageField.TYPE, MessageType.CLIENT_HELLO.type)
        put(MessageField.KEY, ownPublicKey.bytes)
    }

    return ClientHelloMessage(
        nonce = nonce,
        data = pack(payloadMap),
    )
}

data class ClientHelloMessage(
    override val nonce: Nonce,
    override val data: Payload
) : Message {

    override val bytes: ByteArray by lazy {
        nonce.bytes + data.bytes
    }
}