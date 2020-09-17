package org.saltyrtc.client.signalling

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.msgpack.jackson.dataformat.MessagePackFactory
import java.io.IOException


actual fun unpackPayloadMap(payload: ByteArray): Map<String, Any> {
    val objectMapper = ObjectMapper(MessagePackFactory())
    val map: Map<String, Any>  = try {
        val ref  = object:TypeReference<Map<String, Any>>(){}
        objectMapper.readValue<Map<String, Any>>(payload, ref)
    } catch (e: IOException) {
        throw Exception("Deserialization failed", e)
    }
    return map
}

actual fun packPayloadMap(payloadMap: HashMap<String, Any>): ByteArray {
    val objectMapper = ObjectMapper(MessagePackFactory())
    return  objectMapper.writeValueAsBytes(payloadMap)
}