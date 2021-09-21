package net.thalerit.saltyrtc.api

import kotlinx.coroutines.flow.StateFlow
import kotlin.jvm.JvmInline

/**
 * A SaltyRTC task is a protocol extension to this protocol that will be negotiated during the client-to-client
 * authentication phase. Once a task has been negotiated and the authentication is complete, the task protocol defines
 * further procedures, messages, etc.
 */
interface Task<T : Connection> {
    val url: TaskUrl

    val connection: StateFlow<Result<T>?>

    /**
     * Once the client2client handshake is completed, the open signalling channel will be passed to the task, and the
     * task attempts to a connection.
     */
    fun openConnection(channel: SignallingChannel, data: Any?)
    fun handleClosed(reason: CloseReason)

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