package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.Cookie
import org.saltyrtc.client.Nonce
import org.saltyrtc.client.Subprotocols
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.crypto.SharedKey
import org.saltyrtc.client.crypto.encrypt
import org.saltyrtc.client.entity.Payload
import org.saltyrtc.client.entity.pack

/**
 * {
 * "type": "client-auth",
 * "your_cookie": b"af354da383bba00507fa8f289a20308a",
 * "subprotocols": [
 * "v1.saltyrtc.org",
 * "some.other.protocol"
 * ],
 * "ping_interval": 30,
 * "your_key": b"2659296ce03993e876d5f2abcaa6d19f92295ff119ee5cb327498d2620efc979"
}
 */
@OptIn(ExperimentalStdlibApi::class)
fun clientAuthMessage(
    nonce: Nonce,
    serverCookie: Cookie,
    serverPublicKey: PublicKey,
    sharedKey: SharedKey,
): ClientAuthMessage {
    val payloadMap: Map<MessageField, Any> = buildMap {
        put(MessageField.TYPE, MessageType.CLIENT_AUTH.type)
        put(MessageField.YOUR_COOKIE, serverCookie.bytes)
        put(MessageField.SUBPROTOCOLS, listOf(Subprotocols.V1_SALTYRTC_ORG))
        put(MessageField.PING_INTERVAL, 30) // TODO configurable?
        put(MessageField.YOUR_KEY, serverPublicKey.bytes)
    }
    val payload = pack(payloadMap)
    val encryptedData = encrypt(payload, nonce, sharedKey)

    return ClientAuthMessage(
        nonce = nonce,
        data = Payload(encryptedData.bytes),
    )
}

data class ClientAuthMessage(
    override val nonce: Nonce,
    override val data: Payload,
) : Message {
    override val bytes: ByteArray by lazy {
        nonce.bytes + data.bytes
    }
}