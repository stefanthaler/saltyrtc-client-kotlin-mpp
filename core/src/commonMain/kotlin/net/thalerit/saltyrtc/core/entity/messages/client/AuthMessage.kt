package net.thalerit.saltyrtc.core.entity.messages.client

import net.thalerit.crypto.CipherText
import net.thalerit.crypto.SharedKey
import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.entity.message
import net.thalerit.saltyrtc.core.entity.messages.server.MessageField
import net.thalerit.saltyrtc.core.entity.messages.server.MessageType
import net.thalerit.saltyrtc.core.entity.pack
import net.thalerit.saltyrtc.core.entity.unpack
import net.thalerit.saltyrtc.core.util.requireFields
import net.thalerit.saltyrtc.core.util.requireType
import net.thalerit.saltyrtc.crypto.decrypt
import net.thalerit.saltyrtc.crypto.encrypt


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


