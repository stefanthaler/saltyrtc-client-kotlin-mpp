package org.saltyrtc.client.entity.messages.client

import org.saltyrtc.client.Nonce
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.api.requireFields
import org.saltyrtc.client.api.requireType
import org.saltyrtc.client.crypto.*
import org.saltyrtc.client.entity.Payload
import org.saltyrtc.client.entity.message
import org.saltyrtc.client.entity.messages.MessageField
import org.saltyrtc.client.entity.messages.MessageType
import org.saltyrtc.client.entity.pack
import org.saltyrtc.client.entity.unpack

fun keyMessage(
    it: Message,
    sharedKey: SharedKey,
    nonce: Nonce,
): KeyMessage {
    val plainText = decrypt(CipherText(it.data.bytes), nonce, sharedKey)
    val payloadMap = unpack(Payload(plainText.bytes))

    payloadMap.requireType(MessageType.KEY)
    payloadMap.requireFields(MessageField.KEY)

    val publicKey = PublicKey(MessageField.key(payloadMap))
    return KeyMessage(publicKey)
}

@OptIn(ExperimentalStdlibApi::class)
fun keyMessage(
    clientSessionKey: PublicKey,
    sharedKey: SharedKey,
    nonce: Nonce,
): Message {
    val payloadMap: Map<MessageField, Any> = buildMap {
        put(MessageField.TYPE, MessageType.KEY.type)
        put(MessageField.KEY, clientSessionKey.bytes)
    }

    val payload = pack(payloadMap)
    val encryptedData = encrypt(payload, nonce, sharedKey)

    return message(
        nonce = nonce,
        data = Payload(encryptedData.bytes),
    )
}

data class KeyMessage(
    val clientSession: PublicKey
)
