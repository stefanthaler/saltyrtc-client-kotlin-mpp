package net.thalerit.saltyrtc.core.entity.messages.client

import net.thalerit.crypto.CipherText
import net.thalerit.crypto.SharedKey
import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.entity.unpack
import net.thalerit.saltyrtc.core.util.requireFields
import net.thalerit.saltyrtc.core.util.requireType
import net.thalerit.saltyrtc.crypto.decrypt

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
): PayloadMap {
    return buildMap {
        put(MessageField.TYPE, MessageType.APPLICATION.type)
        put(MessageField.DATA, data)
    }
}