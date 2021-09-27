package net.thalerit.saltyrtc

import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessagePacker
import net.thalerit.saltyrtc.api.Payload

val defaultMsgPacker = object : MessagePacker {
    override fun unpack(payload: Payload): Map<MessageField, Any> = platformUnpack(payload)

    override fun pack(payloadMap: Map<MessageField, Any>): Payload = platformPack(payloadMap)
}

internal expect fun platformUnpack(payload: Payload): Map<MessageField, Any>

internal expect fun platformPack(payloadMap: Map<MessageField, Any>): Payload

