package net.thalerit.saltyrtc.core.entity.messages.relayed

import net.thalerit.crypto.CipherText
import net.thalerit.crypto.SharedKey
import net.thalerit.saltyrtc.api.Message
import net.thalerit.saltyrtc.api.Nonce
import net.thalerit.saltyrtc.api.Payload
import net.thalerit.saltyrtc.core.entity.message
import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.core.entity.pack
import net.thalerit.saltyrtc.core.entity.unpack
import net.thalerit.saltyrtc.core.util.requireFields
import net.thalerit.saltyrtc.core.util.requireType
import net.thalerit.saltyrtc.crypto.decrypt
import net.thalerit.saltyrtc.crypto.encrypt

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