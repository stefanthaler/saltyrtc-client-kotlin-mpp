package net.thalerit.saltyrtc.core.entity.messages.server

import net.thalerit.crypto.PublicKey
import net.thalerit.saltyrtc.api.Message
import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.core.entity.unpack
import net.thalerit.saltyrtc.core.util.requireFields
import net.thalerit.saltyrtc.core.util.requireType
import net.thalerit.saltyrtc.crypto.publicKey

fun serverHelloMessage(message: Message): ServerHelloMessage {
    val payloadMap = unpack(message.data)

    payloadMap.requireType(MessageType.SERVER_HELLO)
    payloadMap.requireFields(MessageField.KEY)

    return ServerHelloMessage(
        key = publicKey(MessageField.key(payloadMap))
    )
}

data class ServerHelloMessage(
    val key: PublicKey
)