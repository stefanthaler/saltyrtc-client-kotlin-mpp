package org.saltyrtc.client.entity

import org.saltyrtc.client.entity.messages.MessageField
import org.saltyrtc.client.entity.messages.MessageType
import kotlin.jvm.JvmInline

@JvmInline
value class Payload(val bytes: ByteArray)

expect fun unpack(payload: Payload) : Map<MessageField, Any>

expect fun pack(payloadMap: Map<MessageField, Any>): Payload