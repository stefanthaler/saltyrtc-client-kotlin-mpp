package net.thalerit.saltyrtc.core.entity.messages.client

import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.message
import net.thalerit.saltyrtc.core.entity.messages.key
import net.thalerit.saltyrtc.core.util.requireFields
import net.thalerit.saltyrtc.core.util.requireType
import net.thalerit.saltyrtc.crypto.*

fun SaltyRtcClient.clientSessionKeyMessage(
    it: Message,
    sharedKey: SharedKey,
    nonce: Nonce,
): KeyMessage {
    val plainText = decrypt(CipherText(it.data.bytes), nonce, sharedKey)
    val payloadMap = unpack(Payload(plainText.bytes))

    payloadMap.requireType(MessageType.KEY)
    payloadMap.requireFields(MessageField.KEY)

    val publicKey = publicKey(MessageField.key(payloadMap))
    return KeyMessage(publicKey)
}

@OptIn(ExperimentalStdlibApi::class)
fun SaltyRtcClient.clientSessionKeyMessage(
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
