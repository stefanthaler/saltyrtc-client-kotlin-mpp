package org.saltyrtc.client.entity.messages

import org.saltyrtc.client.entity.CloseReason
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
    REASON

    ;

    companion object {
        fun type(payloadMap: Map<MessageField, Any>) = MessageType.valueOfType(payloadMap[TYPE] as String)
        fun key(payloadMap: Map<MessageField, Any>) = payloadMap[KEY] as ByteArray
        fun yourCookie(payloadMap: Map<MessageField, Any>) = cookie(payloadMap[YOUR_COOKIE] as ByteArray)
        fun isInitiatorConnected(payloadMap: Map<MessageField, Any>) = payloadMap[INITIATOR_CONNECTED] as Boolean?
        fun responders(payloadMap: Map<MessageField, Any>) = payloadMap[RESPONDERS] as List<Identity>?
        fun signedKeys(payloadMap: Map<MessageField, Any>) = payloadMap[SIGNED_KEYS] as ByteArray
        fun id(payloadMap: Map<MessageField, Any>) = Identity((payloadMap[ID] as Int).toByte())
        fun reason(payloadMap: Map<MessageField, Any>) = CloseReason.valueOf(payloadMap[REASON] as String)
    }
}