package net.thalerit.saltyrtc.core.entity.messages.server

import net.thalerit.saltyrtc.api.*
import net.thalerit.saltyrtc.core.entity.cookie

enum class MessageField {
    TYPE,
    KEY,
    YOUR_COOKIE,
    SUBPROTOCOLS,
    PING_INTERVAL,
    YOUR_KEY,
    SIGNED_KEYS,
    INITIATOR_CONNECTED,
    RESPONDERS,
    ID,
    REASON,
    TASKS,
    TASK,
    DATA,

    //relayed task
    P,
    ;

    companion object {
        fun type(payloadMap: Map<MessageField, Any>) = MessageType.valueOfType(payloadMap[TYPE] as String)
        fun key(payloadMap: Map<MessageField, Any>) = payloadMap[KEY] as ByteArray
        fun yourCookie(payloadMap: Map<MessageField, Any>) = cookie(payloadMap[YOUR_COOKIE] as ByteArray)

        fun isInitiatorConnected(payloadMap: Map<MessageField, Any>) = payloadMap[INITIATOR_CONNECTED] as Boolean?
        fun responders(payloadMap: Map<MessageField, Any>) =
            (payloadMap[RESPONDERS] as List<Int>?)?.map { Identity(it.toByte()) }

        fun signedKeys(payloadMap: Map<MessageField, Any>) = payloadMap[SIGNED_KEYS] as ByteArray
        fun id(payloadMap: Map<MessageField, Any>) = Identity((payloadMap[ID] as Int).toByte())
        fun reason(payloadMap: Map<MessageField, Any>) = CloseReason.valueOf(payloadMap[REASON] as String)
        fun tasks(payloadMap: Map<MessageField, Any>) =
            (payloadMap[TASKS] as List<String>?)?.map { SupportedTask.valueOfUrl(TaskUrl(it)) }

        fun task(payloadMap: Map<MessageField, Any>) =
            (payloadMap[TASK] as String?)?.let { SupportedTask.valueOfUrl(TaskUrl(it)) }

        fun data(payloadMap: Map<MessageField, Any>) = (payloadMap[DATA] as Map<String, Any>).map { (key, entry) ->
            SupportedTask.valueOfUrl(TaskUrl(key)) to entry
        }.toMap()

        fun p(payloadMap: Map<MessageField, Any>) = Payload(payloadMap[P] as ByteArray)
    }
}
