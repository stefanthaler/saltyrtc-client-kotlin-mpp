package org.saltyrtc.client.entity

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.msgpack.jackson.dataformat.MessagePackFactory
import org.saltyrtc.client.entity.messages.MessageField
import java.io.IOException

actual fun unpack(payload: Payload): Map<MessageField, Any> {
    val objectMapper = ObjectMapper(MessagePackFactory())
    val map: Map<String, Any> = try {
        val ref = object : TypeReference<Map<String, Any>>() {}
        objectMapper.readValue(payload.bytes, ref)
    } catch (e: IOException) {
        throw Exception("Deserialization failed", e)
    }
    return map.map { MessageField.valueOf(it.key) to it.value }.toMap()
}

actual fun pack(payloadMap: Map<MessageField, Any>): Payload {
    val map = payloadMap.map { it.key.name to it.value }.toMap()
    val objectMapper = ObjectMapper(MessagePackFactory())
    return Payload(objectMapper.writeValueAsBytes(map))
}