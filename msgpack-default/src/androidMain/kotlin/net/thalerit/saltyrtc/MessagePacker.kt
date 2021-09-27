@file:JvmName("AndroidMessagePacker")

package net.thalerit.saltyrtc

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import net.thalerit.saltyrtc.api.MessageField
import net.thalerit.saltyrtc.api.Payload
import org.msgpack.jackson.dataformat.MessagePackFactory
import java.io.IOException

internal actual fun platformUnpack(payload: Payload): Map<MessageField, Any> {
    val objectMapper = ObjectMapper(MessagePackFactory())
    val map: Map<String, Any> = try {
        val ref = object : TypeReference<Map<String, Any>>() {}
        objectMapper.readValue(payload.bytes, ref)
    } catch (e: IOException) {
        throw Exception("Deserialization failed", e)
    }
    return map.map { MessageField.valueOf(it.key.uppercase()) to it.value }.toMap()
}

internal actual fun platformPack(payloadMap: Map<MessageField, Any>): Payload {
    val map = payloadMap.map { it.key.name.lowercase() to it.value }.toMap()
    val objectMapper = ObjectMapper(MessagePackFactory())
    return Payload(objectMapper.writeValueAsBytes(map))
}