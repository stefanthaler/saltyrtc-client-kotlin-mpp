package org.saltyrtc.client.signalling

import org.saltyrtc.client.signalling.messages.ServerHelloMessage


enum class SignallingMessageTypes(val type:String) {
    SERVER_HELLO("server-hello") {
        override fun create(nonce: Nonce, map: Map<String, Any>): SignallingMessage {
            return ServerHelloMessage(type, nonce, map)
        }
    },
    CLIENT_HELLO("client-hello") {
        override fun create(nonce: Nonce, map: Map<String, Any>): SignallingMessage {
            TODO("Not yet implemented")
        }
    },
    CLIENT_AUTH("client-auth") {
        override fun create(nonce: Nonce, map: Map<String, Any>): SignallingMessage {
            TODO("Not yet implemented")
        }
    },
    SERVER_AUTH("server-auth") {
        override fun create(nonce: Nonce, map: Map<String, Any>): SignallingMessage {
            TODO("Not yet implemented")
        }
    },
    NEW_INITIATOR("new-initiator") {
        override fun create(nonce: Nonce, map: Map<String, Any>): SignallingMessage {
            TODO("Not yet implemented")
        }
    },
    NEW_RESPONDER("new-responder") {
        override fun create(nonce: Nonce, map: Map<String, Any>): SignallingMessage {
            TODO("Not yet implemented")
        }
    },
    DROP_RESPONDER("drop-responder") {
        override fun create(nonce: Nonce, map: Map<String, Any>): SignallingMessage {
            TODO("Not yet implemented")
        }
    },
    DISCONNECTED("disconnected") {
        override fun create(nonce: Nonce, map: Map<String, Any>): SignallingMessage {
            TODO("Not yet implemented")
        }
    },
    SEND_ERROR("send-error") {
        override fun create(nonce: Nonce, map: Map<String, Any>): SignallingMessage {
            TODO("Not yet implemented")
        }
    },
    TOKEN("token") {
        override fun create(nonce: Nonce, map: Map<String, Any>): SignallingMessage {
            TODO("Not yet implemented")
        }
    },
    KEY("key") {
        override fun create(nonce: Nonce, map: Map<String, Any>): SignallingMessage {
            TODO("Not yet implemented")
        }
    },
    AUTH("auth") {
        override fun create(nonce: Nonce, map: Map<String, Any>): SignallingMessage {
            TODO("Not yet implemented")
        }
    },
    APPLICATION("application") {
        override fun create(nonce: Nonce, map: Map<String, Any>): SignallingMessage {
            TODO("Not yet implemented")
        }
    },
    TASK("task") {
        override fun create(nonce: Nonce, map: Map<String, Any>): SignallingMessage {
            TODO("Not yet implemented")
        }
    };

    abstract fun create(nonce: Nonce, map:Map<String, Any>) : SignallingMessage

    companion object {
        private val map = SignallingMessageTypes.values().associateBy(SignallingMessageTypes::type)
        fun from(type: String) = map[type]
    }
}

