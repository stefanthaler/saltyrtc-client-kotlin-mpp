package net.thalerit.saltyrtc

import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessagePacker
import net.thalerit.saltyrtc.api.Payload

val defaultMsgPacker = object : MessagePacker {
    override fun unpack(payload: Payload): Map<MessageField, Any> = unpack(payload)

    override fun pack(payloadMap: Map<MessageField, Any>): Payload = pack(payloadMap)
}

expect fun unpack(payload: Payload): Map<MessageField, Any>

expect fun pack(payloadMap: Map<MessageField, Any>): Payload

