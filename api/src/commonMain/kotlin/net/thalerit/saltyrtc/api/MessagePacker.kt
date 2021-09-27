package net.thalerit.saltyrtc.api

interface MessagePacker {
    fun unpack(payload: Payload): Map<MessageField, Any>

    fun pack(payloadMap: Map<MessageField, Any>): Payload
}