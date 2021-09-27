package net.thalerit.saltyrtc.core.entity.messages.client

import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.message
import net.thalerit.saltyrtc.core.entity.messages.reason
import net.thalerit.saltyrtc.core.util.requireFields
import net.thalerit.saltyrtc.core.util.requireType
import net.thalerit.saltyrtc.crypto.CipherText
import net.thalerit.saltyrtc.crypto.SharedKey
import net.thalerit.saltyrtc.crypto.decrypt
import net.thalerit.saltyrtc.crypto.encrypt


fun SaltyRtcClient.closeMessage(
    it: Message,
    sharedKey: SharedKey,
): CloseMessage {
    val plainText = decrypt(CipherText(it.data.bytes), it.nonce, sharedKey)
    val payloadMap = unpack(Payload(plainText.bytes))

    payloadMap.requireType(MessageType.CLOSE)
    payloadMap.requireFields(MessageField.REASON)

    return CloseMessage(MessageField.reason(payloadMap))
}

@OptIn(ExperimentalStdlibApi::class)
fun SaltyRtcClient.closeMessage(
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
