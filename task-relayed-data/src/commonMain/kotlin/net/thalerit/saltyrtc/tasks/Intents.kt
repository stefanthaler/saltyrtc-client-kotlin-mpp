package net.thalerit.saltyrtc.tasks

import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessageType
import net.thalerit.saltyrtc.api.PayloadMap
import net.thalerit.saltyrtc.api.TaskIntent

sealed class RelayedDataIntent : TaskIntent {
    data class SendData(
        val data: Any
    ) : RelayedDataIntent() {
        override val type: MessageType by lazy {
            MessageType.DATA
        }

        @OptIn(ExperimentalStdlibApi::class)
        override val payloadMap: PayloadMap by lazy {
            buildMap {
                put(MessageField.TYPE, type)
                put(MessageField.P, data)
            }
        }
    }

    data class SendApplication(
        val data: Any
    ) : RelayedDataIntent() {
        override val type: MessageType by lazy {
            MessageType.APPLICATION
        }

        @OptIn(ExperimentalStdlibApi::class)
        override val payloadMap: PayloadMap by lazy {
            buildMap {
                put(MessageField.TYPE, type)
                put(MessageField.DATA, data)
            }
        }
    }
}