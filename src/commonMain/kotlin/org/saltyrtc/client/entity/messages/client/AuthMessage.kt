package org.saltyrtc.client.entity.messages.client

import org.saltyrtc.client.api.*
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

fun authMessage(
    it: Message,
    sharedKey: SharedKey,
    isInitiator: Boolean,
): AuthMessage {
    val plainText = decrypt(CipherText(it.data.bytes), it.nonce, sharedKey)
    val payloadMap = unpack(Payload(plainText.bytes))

    payloadMap.requireType(MessageType.AUTH)
    payloadMap.requireFields(MessageField.YOUR_COOKIE, MessageField.DATA)

    if (isInitiator) {
        payloadMap.requireFields(MessageField.TASKS)
    } else {
        payloadMap.requireFields(MessageField.TASK)
    }

    return AuthMessage(
        yourCookie = MessageField.yourCookie(payloadMap),
        tasks = MessageField.tasks(payloadMap),
        task = MessageField.task(payloadMap),
        data = MessageField.data(payloadMap),
    )
}

@OptIn(ExperimentalStdlibApi::class)
fun authMessage(
    sessionSharedKey: SharedKey,
    nonce: Nonce,
    yourCookie: Cookie, // to other clients cookie
    task: SupportedTask?,
    tasks: List<SupportedTask>?,
    data: Map<SupportedTask, Any>,
): Message {
    val payloadMap: Map<MessageField, Any> = buildMap {
        put(MessageField.TYPE, MessageType.AUTH.type)
        put(MessageField.YOUR_COOKIE, yourCookie.bytes)
        if (task != null) {
            put(MessageField.TASK, task.taskUrl.url)
        }
        if (tasks != null) {
            put(MessageField.TASKS, tasks.map { it.taskUrl.url })
        }
        put(MessageField.DATA, data.map { it.key.taskUrl.url to it.value }.toMap())
    }

    val payload = pack(payloadMap)
    val encryptedData = encrypt(payload, nonce, sessionSharedKey)

    return message(
        nonce = nonce,
        data = Payload(encryptedData.bytes),
    )
}


data class AuthMessage(
    val yourCookie: Cookie,
    val tasks: List<SupportedTask>?,
    val task: SupportedTask?,
    val data: Map<SupportedTask, Any>
)


