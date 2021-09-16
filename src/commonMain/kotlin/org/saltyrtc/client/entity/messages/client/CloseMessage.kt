package org.saltyrtc.client.entity.messages.client

import org.saltyrtc.client.Nonce
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.api.requireFields
import org.saltyrtc.client.api.requireType
import org.saltyrtc.client.crypto.CipherText
import org.saltyrtc.client.crypto.SharedKey
import org.saltyrtc.client.crypto.decrypt
import org.saltyrtc.client.crypto.encrypt
import org.saltyrtc.client.entity.*
import org.saltyrtc.client.entity.messages.MessageField
import org.saltyrtc.client.entity.messages.MessageType

fun closeMessage(
    it: Message,
    sharedKey: SharedKey
): CloseMessage {
    val plainText = decrypt(CipherText(it.data.bytes), it.nonce, sharedKey)
    val payloadMap = unpack(Payload(plainText.bytes))

    payloadMap.requireType(MessageType.CLOSE)
    payloadMap.requireFields(MessageField.REASON)

    return CloseMessage(MessageField.reason(payloadMap))
}

@OptIn(ExperimentalStdlibApi::class)
fun closeMessage(
    reason: CloseReason,
    sharedKey: SharedKey,
    nonce: Nonce,
): Message {
    val payloadMap: Map<MessageField, Any> = buildMap {
        put(MessageField.TYPE, MessageType.CLOSE.type)
        put(MessageField.REASON, reason.reason)
    }

    val payload = pack(payloadMap)
    val encryptedData = encrypt(payload, nonce, sharedKey)

    return message(
        nonce = nonce,
        data = Payload(encryptedData.bytes),
    )
}

data class CloseMessage(
    val reason: CloseReason
)
