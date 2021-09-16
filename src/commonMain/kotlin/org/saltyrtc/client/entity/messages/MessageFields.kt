package org.saltyrtc.client.entity.messages

enum class MessageField {
    TYPE,
    KEY,
    ;

    companion object {
        fun type(payloadMap: Map<MessageField, Any>):String = payloadMap[TYPE] as String
        fun key(payloadMap: Map<MessageField, Any>):ByteArray = payloadMap[KEY] as ByteArray
    }
}



