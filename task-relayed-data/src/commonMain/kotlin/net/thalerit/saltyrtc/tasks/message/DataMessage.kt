package net.thalerit.saltyrtc.tasks.message

import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.MessageType

/**
 * Once the task has taken over, the user application of a client MAY trigger sending this message.

 * This message MAY contain any MessagePack value type. If the user does not wish to encode messages with MessagePack,
 * they can be transmitted using MessagePack binary or string value types.

 * A task who sends a 'data' message SHALL set the p field to whatever data the user application provided. The name is
 * an abbreviation that stands for "payload" and has been chosen to reduce the message overhead.

 * A receiving task SHALL validate that the p field is set. It MUST pass the payload inside that field to the user
 * application.
 *
 */
@OptIn(ExperimentalStdlibApi::class)
fun dataMessage(
    data: Any
): Map<MessageField, Any> {
    return buildMap {
        put(MessageField.TYPE, MessageType.DATA.type)
        put(MessageField.P, data)
    }
}