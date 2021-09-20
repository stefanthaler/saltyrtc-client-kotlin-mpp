package net.thalerit.saltyrtc.core.entity.messages.client

import net.thalerit.crypto.CipherText
import net.thalerit.crypto.SharedKey
import net.thalerit.saltyrtc.api.Message
import net.thalerit.saltyrtc.api.Nonce
import net.thalerit.saltyrtc.api.Payload
import net.thalerit.saltyrtc.core.entity.message
import net.thalerit.saltyrtc.core.entity.messages.server.MessageField
import net.thalerit.saltyrtc.core.entity.messages.server.MessageType
import net.thalerit.saltyrtc.core.entity.pack
import net.thalerit.saltyrtc.core.entity.unpack
import net.thalerit.saltyrtc.core.util.requireFields
import net.thalerit.saltyrtc.core.util.requireType
import net.thalerit.saltyrtc.crypto.decrypt
import net.thalerit.saltyrtc.crypto.encrypt

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