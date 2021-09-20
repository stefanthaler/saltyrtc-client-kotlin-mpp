package org.saltyrtc.client.entity.messages.relayed

import org.saltyrtc.client.api.Message
import org.saltyrtc.client.api.Nonce
import org.saltyrtc.client.util.requireFields
import org.saltyrtc.client.util.requireType
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

fun dataMessage(
    it: Message,
    sharedKey: SharedKey
): DataMessage {
    val plainText = decrypt(CipherText(it.data.bytes), it.nonce, sharedKey)
    val payloadMap = unpack(Payload(plainText.bytes))

    payloadMap.requireType(MessageType.DATA)
    payloadMap.requireFields(MessageField.P)
    return DataMessage(
        p = MessageField.p(payloadMap)
    )
}

@OptIn(ExperimentalStdlibApi::class)
fun dataMessage(
    data: Payload,
    sharedKey: SharedKey,
    nonce: Nonce,
): Message {
    val payloadMap: Map<MessageField, Any> = buildMap {
        put(MessageField.TYPE, MessageType.DATA.type)
        put(MessageField.P, data)
    }

    val payload = pack(payloadMap)
    val encryptedData = encrypt(payload, nonce, sharedKey)

    return message(
        nonce = nonce,
        data = Payload(encryptedData.bytes),
    )
}

data class DataMessage(
    val p: Payload
)