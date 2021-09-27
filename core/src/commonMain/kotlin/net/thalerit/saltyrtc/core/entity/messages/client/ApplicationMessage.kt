package net.thalerit.saltyrtc.core.entity.messages.client

import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.util.requireFields
import net.thalerit.saltyrtc.core.util.requireType
import net.thalerit.saltyrtc.crypto.CipherText
import net.thalerit.saltyrtc.crypto.SharedKey
import net.thalerit.saltyrtc.crypto.decrypt

fun SaltyRtcClient.applicationMessage(
    it: Message,
    sharedKey: SharedKey,
): ApplicationMessage {
    val plainText = decrypt(CipherText(it.data.bytes), it.nonce, sharedKey)
    val payloadMap = unpack(Payload(plainText.bytes))

    payloadMap.requireType(MessageType.APPLICATION)
    payloadMap.requireFields(MessageField.DATA)
    return ApplicationMessage(
        payloadMap
    )
}

@OptIn(ExperimentalStdlibApi::class)
fun applicationMessage(
    data: Any,
): PayloadMap {
    return buildMap {
        put(MessageField.TYPE, MessageType.APPLICATION.type)
        put(MessageField.DATA, data)
    }
}