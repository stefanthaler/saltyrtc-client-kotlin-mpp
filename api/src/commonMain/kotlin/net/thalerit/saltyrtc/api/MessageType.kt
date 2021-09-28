package net.thalerit.saltyrtc.api

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

    // client2client
    TOKEN("token"), // TODO
    KEY("key"),
    AUTH("auth"),
    APPLICATION("application"),
    CLOSE("close"),


    // relayed data
    DATA("data"),
    ;

    companion object {
        private val byType by lazy {
            values().associateBy { it.type }
        }

        fun valueOfType(type: String): MessageType {
            return byType[type.lowercase()]!!
        }
    }
}



