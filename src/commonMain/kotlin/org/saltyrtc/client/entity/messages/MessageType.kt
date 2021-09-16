package org.saltyrtc.client.entity.messages

private val byType by lazy {
    MessageType.values().associateBy { it.type }
}

enum class MessageType(val type: String) {
    //client2server
    SERVER_HELLO("server-hello"),
    CLIENT_AUTH("client-auth"),
    CLIENT_HELLO("client-hello"),
    SERVER_AUTH("server-auth"),
    NEW_RESPONDER("new-responder"),
    NEW_INITIATOR("new-initiator"),
    DROP_RESPONDER("drop-responder"),
    SEND_ERROR("send-error"),
    DISCONNECTED("disconnected"),
    ;

    companion object {
        fun valueOfType(type: String): MessageType = byType[type]!!
    }
}
