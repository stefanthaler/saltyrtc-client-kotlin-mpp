package net.thalerit.saltyrtc.api

import kotlin.jvm.JvmInline

/**
 * A SaltyRTC task is a protocol extension to this protocol that will be negotiated during the client-to-client
 * authentication phase. Once a task has been negotiated and the authentication is complete, the task protocol defines
 * further procedures, messages, etc.
 */
interface Task {
    val url: TaskUrl

    /**
     * Once the client2client handshake is completed, the open signalling channel will be passed to the task, and the
     * task attempts to a connection.
     */
    fun openConnection(channel: SignallingChannel, data: Any?)
    fun handleClosed(reason: CloseReason)

    /**
     * Actions that the task supports once it is opened, e.g. SendOffer
     */
    fun handle(intent: TaskIntent)

    /**
     * Whether an incoming task message should be published to client.
     */
    fun emitToClient(taskMessage: TaskMessage): Boolean

    val authData: Any?
}

val V1_WEBRTC_TASK = TaskUrl("v1.webrtc.tasks.saltyrtc.org")
val V0_RELAYED_DATA = TaskUrl("v0.relayed-data.tasks.saltyrtc.org")

val supportedTasks = listOf(
    V1_WEBRTC_TASK,
    V0_RELAYED_DATA,
)

@JvmInline
value class TaskUrl(val url: String)

interface TaskMessage {
    val type: MessageType
    val payloadMap: PayloadMap
}

interface TaskIntent {
    val type: MessageType
    val payloadMap: PayloadMap
}