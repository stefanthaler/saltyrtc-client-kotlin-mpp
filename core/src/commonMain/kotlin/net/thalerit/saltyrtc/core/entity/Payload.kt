package net.thalerit.saltyrtc.core.entity

import net.thalerit.saltyrtc.api.Payload
import net.thalerit.saltyrtc.core.entity.messages.server.MessageField

expect fun unpack(payload: Payload): Map<MessageField, Any>

expect fun pack(payloadMap: Map<MessageField, Any>): Payload