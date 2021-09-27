package net.thalerit.saltyrtc.core.entity.messages.client

import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.SaltyRtcClient
import net.thalerit.saltyrtc.core.entity.message
import net.thalerit.saltyrtc.core.entity.messages.data
import net.thalerit.saltyrtc.core.entity.messages.task
import net.thalerit.saltyrtc.core.entity.messages.tasks
import net.thalerit.saltyrtc.core.entity.messages.yourCookie
import net.thalerit.saltyrtc.core.pack
import net.thalerit.saltyrtc.core.unpack
import net.thalerit.saltyrtc.core.util.requireFields
import net.thalerit.saltyrtc.core.util.requireType
import net.thalerit.saltyrtc.crypto.CipherText
import net.thalerit.saltyrtc.crypto.SharedKey
import net.thalerit.saltyrtc.crypto.decrypt
import net.thalerit.saltyrtc.crypto.encrypt


fun SaltyRtcClient.authMessage(
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
fun SaltyRtcClient.authMessage(
    sessionSharedKey: SharedKey,
    nonce: Nonce,
    yourCookie: Cookie, // to other clients cookie
    isInitiator: Boolean,
    task: TaskUrl,
    tasks: List<TaskUrl>?,
    data: Any?,
): Message {
    val payloadMap: Map<MessageField, Any> = buildMap {
        put(MessageField.TYPE, MessageType.AUTH.type)
        put(MessageField.YOUR_COOKIE, yourCookie.bytes)
        if (isInitiator) {
            put(MessageField.TASK, task.url)
        } else {
            requireNotNull(tasks)
            put(MessageField.TASKS, tasks.map { it.url })
        }

        put(MessageField.DATA, buildMap<String, Any?> {
            put(task.url, data)
        })
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
    val tasks: List<TaskUrl>?,
    val task: TaskUrl?,
    val data: Map<TaskUrl, Any?>
)