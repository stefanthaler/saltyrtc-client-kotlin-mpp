package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.Cookie
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.api.requireFields
import org.saltyrtc.client.api.requireResponderId
import org.saltyrtc.client.api.requireType
import org.saltyrtc.client.crypto.CipherText
import org.saltyrtc.client.crypto.SharedKey
import org.saltyrtc.client.crypto.decrypt
import org.saltyrtc.client.entity.Payload
import org.saltyrtc.client.entity.unpack
import org.saltyrtc.client.state.Identity
import org.saltyrtc.client.state.InitiatorIdentity

fun serverAuthMessage(
    message: Message,
    isInitiator: Boolean,
    sharedKey: SharedKey,
): ServerAuthMessage {
    val decrypted = decrypt(CipherText(message.data.bytes), message.nonce, sharedKey)
    val payloadMap = unpack(Payload(decrypted.bytes))

    payloadMap.requireType(MessageType.SERVER_AUTH)
    payloadMap.requireFields(MessageField.YOUR_COOKIE, MessageField.SIGNED_KEYS)

    if (isInitiator) {
        payloadMap.requireFields(MessageField.RESPONDERS)
        require(message.nonce.destination == InitiatorIdentity.address)
        MessageField.responders(payloadMap)!!.forEach {
            requireResponderId(it)
        }
    } else {
        payloadMap.requireFields(MessageField.INITIATOR_CONNECTED)
        requireResponderId(Identity(message.nonce.destination))
    }

    return ServerAuthMessage(
        yourCookie = MessageField.yourCookie(payloadMap),
        identity = Identity(message.nonce.destination),
        isInitiatorConnected = MessageField.isInitiatorConnected(payloadMap),
        responders = MessageField.responders(payloadMap),
        signedKeys = MessageField.signedKeys(payloadMap),
    )
}

/**
 * {
 * "type": "server-auth",
 * "your_cookie": b"18b96fd5a151eae23e8b5a1aed2fe30d",
 * "signed_keys": b"e42bfd8c5bc9870ae1a0d928d52810983ac7ddf69df013a7621d072aa9633616cfd...",
 * "initiator_connected": true,  // ONLY towards responders
 * "responders": [  // ONLY towards initiators
 * 0x02,
 * 0x03
 * ]
 * }
 */
data class ServerAuthMessage(
    val yourCookie: Cookie,
    val identity: Identity,
    val isInitiatorConnected: Boolean? = null,
    val responders: List<Identity>? = null,
    val signedKeys: ByteArray
)