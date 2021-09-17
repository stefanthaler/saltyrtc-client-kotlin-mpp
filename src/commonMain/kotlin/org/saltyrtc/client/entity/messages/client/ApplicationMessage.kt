package org.saltyrtc.client.entity.messages.client

import org.saltyrtc.client.api.Message
import org.saltyrtc.client.api.Nonce
import org.saltyrtc.client.api.requireFields
import org.saltyrtc.client.api.requireType
import org.saltyrtc.client.crypto.CipherText
import org.saltyrtc.client.crypto.SharedKey
import org.saltyrtc.client.crypto.decrypt
import org.saltyrtc.client.crypto.encrypt
import org.saltyrtc.client.entity.Payload
import org.saltyrtc.client.entity.message
import org.saltyrtc.client.entity.messages.server.MessageField
import org.saltyrtc.client.entity.messages.server.MessageType
import org.saltyrtc.client.entity.pack
import org.saltyrtc.client.entity.unpack

fun applicationMessage(
    it: Message,
    sharedKey: SharedKey
): ApplicationMessage {
    val plainText = decrypt(CipherText(it.data.bytes), it.nonce, sharedKey)
    val payloadMap = unpack(Payload(plainText.bytes))

    payloadMap.requireType(MessageType.APPLICATION)
    payloadMap.requireFields(MessageField.DATA)
    return ApplicationMessage(
        data = payloadMap[MessageField.DATA]!!
    )
}

@OptIn(ExperimentalStdlibApi::class)
fun applicationMessage(
    data: Any,
    sharedKey: SharedKey,
    nonce: Nonce,
): Message {
    val payloadMap: Map<MessageField, Any> = buildMap {
        put(MessageField.TYPE, MessageType.CLOSE.type)
        put(MessageField.DATA, data)
    }

    val payload = pack(payloadMap)
    val encryptedData = encrypt(payload, nonce, sharedKey)

    return message(
        nonce = nonce,
        data = Payload(encryptedData.bytes),
    )
}

data class ApplicationMessage(
    val data: Any
)
