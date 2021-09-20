package net.thalerit.saltyrtc.api

import kotlinx.coroutines.flow.StateFlow
import kotlin.jvm.JvmInline

interface Task<T : Connection> {
    val url: TaskUrl

    val connection: StateFlow<T?>

    /**
     * Once the client2client handshake is completed, the open signalling channel will be passed to the task.
     */
    fun handleOpened(channel: SignallingChannel)
    fun handleClosed(reason: CloseReason)
}

val V1_WEBRTC_TASK = TaskUrl("v1.webrtc.tasks.saltyrtc.org")
val V0_RELAYED_DATA = TaskUrl("v0.relayed-data.tasks.saltyrtc.org")

val supportedTasks = listOf(
    V1_WEBRTC_TASK,
    V0_RELAYED_DATA,
    // TODO ORTC task
)

@JvmInline
value class TaskUrl(val url: String)