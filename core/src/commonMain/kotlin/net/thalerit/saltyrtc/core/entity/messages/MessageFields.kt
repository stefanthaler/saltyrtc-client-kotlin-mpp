package net.thalerit.saltyrtc.core.entity.messages

import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.entity.cookie


private val tasksByUrl = supportedTasks.associateBy { it.url }

fun MessageField.Companion.type(payloadMap: Map<MessageField, Any>) =
    MessageType.valueOfType(payloadMap[MessageField.TYPE] as String)

fun MessageField.Companion.key(payloadMap: Map<MessageField, Any>) = payloadMap[MessageField.KEY] as ByteArray
fun MessageField.Companion.yourCookie(payloadMap: Map<MessageField, Any>) =
    cookie(payloadMap[MessageField.YOUR_COOKIE] as ByteArray)

fun MessageField.Companion.isInitiatorConnected(payloadMap: Map<MessageField, Any>) =
    payloadMap[MessageField.INITIATOR_CONNECTED] as Boolean?

fun MessageField.Companion.responders(payloadMap: Map<MessageField, Any>) =
    (payloadMap[MessageField.RESPONDERS] as List<Int>?)?.map { Identity(it.toByte()) }

fun MessageField.Companion.signedKeys(payloadMap: Map<MessageField, Any>) =
    payloadMap[MessageField.SIGNED_KEYS] as ByteArray

fun MessageField.Companion.id(payloadMap: Map<MessageField, Any>) =
    Identity((payloadMap[MessageField.ID] as Int).toByte())

fun MessageField.Companion.reason(payloadMap: Map<MessageField, Any>) =
    CloseReason.valueOf(payloadMap[MessageField.REASON] as String)

fun MessageField.Companion.tasks(payloadMap: Map<MessageField, Any>) =
    (payloadMap[MessageField.TASKS] as List<String>?)?.map { tasksByUrl[it] }

fun MessageField.Companion.task(payloadMap: Map<MessageField, Any>) =
    (payloadMap[MessageField.TASK] as String?)?.let { tasksByUrl[it] }

fun MessageField.Companion.data(payloadMap: Map<MessageField, Any>) =
    (payloadMap[MessageField.DATA] as Map<String, Any>).map { (key, entry) ->
        tasksByUrl[key] to entry
    }.toMap()

fun MessageField.Companion.p(payloadMap: Map<MessageField, Any>) = Payload(payloadMap[MessageField.P] as ByteArray)
