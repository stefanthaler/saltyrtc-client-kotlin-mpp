package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.entity.CloseReason
import org.saltyrtc.client.entity.Task
import org.saltyrtc.client.entity.cookie
import org.saltyrtc.client.state.Identity

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
            (payloadMap[TASKS] as List<String>?)?.map { Task.valueOfUrl(it) }

        fun task(payloadMap: Map<MessageField, Any>) = (payloadMap[TASK] as String?)?.let { Task.valueOfUrl(it) }
        fun data(payloadMap: Map<MessageField, Any>) = (payloadMap[DATA] as Map<String, Any>).map { (key, entry) ->
            Task.valueOfUrl(key) to entry
        }.toMap()
    }
}
