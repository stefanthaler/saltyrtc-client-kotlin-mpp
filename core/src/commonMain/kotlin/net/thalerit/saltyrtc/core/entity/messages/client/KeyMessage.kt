package net.thalerit.saltyrtc.core.entity.messages.client

import net.thalerit.crypto.CipherText
import net.thalerit.crypto.PublicKey
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
import net.thalerit.saltyrtc.crypto.publicKey

fun clientSessionKeyMessage(
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
fun clientSessionKeyMessage(
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
