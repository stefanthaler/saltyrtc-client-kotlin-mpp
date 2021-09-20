package net.thalerit.saltyrtc.core.entity.messages.server

import net.thalerit.crypto.CipherText
import net.thalerit.crypto.SharedKey
import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.entity.unpack
import net.thalerit.saltyrtc.core.state.LastMessageSentTimeStamp
import net.thalerit.saltyrtc.core.util.*
import net.thalerit.saltyrtc.crypto.decrypt

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
        requireInitiatorId(message.nonce.destination)
        MessageField.responders(payloadMap)!!.forEach {
            requireResponderId(it)
        }
    } else {
        payloadMap.requireFields(MessageField.INITIATOR_CONNECTED)
        requireResponderId(message.nonce.destination)
    }

    val responders = MessageField.responders(payloadMap)?.associate {
        it to LastMessageSentTimeStamp(currentTimeInMs())
    }

    return ServerAuthMessage(
        yourCookie = MessageField.yourCookie(payloadMap),
        identity = message.nonce.destination,
        isInitiatorConnected = MessageField.isInitiatorConnected(payloadMap),
        responders = responders,
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
    val responders: Map<Identity, LastMessageSentTimeStamp>? = null,
    val signedKeys: ByteArray
)