package net.thalerit.saltyrtc.core.entity.messages.relayed

import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.message
import net.thalerit.saltyrtc.core.entity.messages.p
import net.thalerit.saltyrtc.core.util.requireFields
import net.thalerit.saltyrtc.core.util.requireType
import net.thalerit.saltyrtc.crypto.CipherText
import net.thalerit.saltyrtc.crypto.SharedKey
import net.thalerit.saltyrtc.crypto.decrypt
import net.thalerit.saltyrtc.crypto.encrypt

fun SaltyRtcClient.dataMessage(
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
fun SaltyRtcClient.dataMessage(
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