package org.saltyrtc.client.entity.messages.client

import org.saltyrtc.client.Cookie
import org.saltyrtc.client.Nonce
import org.saltyrtc.client.api.Message
import org.saltyrtc.client.api.requireFields
import org.saltyrtc.client.api.requireType
import org.saltyrtc.client.crypto.CipherText
import org.saltyrtc.client.crypto.PublicKey
import org.saltyrtc.client.crypto.SharedKey
import org.saltyrtc.client.crypto.decrypt
import org.saltyrtc.client.entity.Payload
import org.saltyrtc.client.entity.messages.MessageField
import org.saltyrtc.client.entity.messages.MessageType
import org.saltyrtc.client.entity.unpack
import kotlin.jvm.JvmInline

fun authMessage(
    it: Message,
    sharedKey: SharedKey,
    nonce: Nonce,
    isInitiator: Boolean,
): KeyMessage {
    val plainText = decrypt(CipherText(it.data.bytes), nonce, sharedKey)
    val payloadMap = unpack(Payload(plainText.bytes))

    payloadMap.requireType(MessageType.AUTH)
    payloadMap.requireFields(MessageField.YOUR_COOKIE, MessageField.DATA)

    if (isInitiator) {
        payloadMap.requireFields(MessageField.TASKS)
    } else {
        payloadMap.requireFields(MessageField.TASK)
    }

    val publicKey = PublicKey(MessageField.key(payloadMap))
    return KeyMessage(publicKey)
}

data class AuthMessage(
    val yourCookie: Cookie,
    val tasks: List<Task>?,
    val task: Task?,
//    val data: TODO
)

@JvmInline
value class Task(val task: String)

