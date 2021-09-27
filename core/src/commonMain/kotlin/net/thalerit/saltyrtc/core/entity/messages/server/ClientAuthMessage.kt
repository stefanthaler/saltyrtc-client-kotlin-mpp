package net.thalerit.saltyrtc.core.entity.messages.server

import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.message
import net.thalerit.saltyrtc.crypto.PublicKey
import net.thalerit.saltyrtc.crypto.SharedKey
import net.thalerit.saltyrtc.crypto.encrypt

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
fun SaltyRtcClient.clientAuthMessage(
    nonce: Nonce,
    serverCookie: Cookie,
    serverPublicKey: PublicKey,
    sharedKey: SharedKey,
): Message {
    val payloadMap: Map<MessageField, Any> = buildMap {
        put(MessageField.TYPE, MessageType.CLIENT_AUTH.type)
        put(MessageField.YOUR_COOKIE, serverCookie.bytes)
        put(MessageField.SUBPROTOCOLS, listOf(SubProtocols.V1_SALTYRTC_ORG))
        put(MessageField.PING_INTERVAL, 30) // TODO configurable?
        put(MessageField.YOUR_KEY, serverPublicKey.bytes)
    }
    val payload = pack(payloadMap)
    val encryptedData = encrypt(payload, nonce, sharedKey)

    return message(
        nonce = nonce,
        data = Payload(encryptedData.bytes),
    )
}