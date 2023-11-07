package com.example.nosqlkotlin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.event.KeyValuePair
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.*
import org.springframework.util.MultiValueMap

@Component
final class JsonClient(
    val mockMvc: MockMvc,
    var objectMapper: ObjectMapper
) {
    inline fun <reified T>post(url: String, body: Any): T {
        return mockMvc.post(url) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.andReturn().asObject()
    }

    inline fun <reified T>get(url: String, vararg queryParameters: Pair<String, String>): T {
        return mockMvc.get(url) {
            contentType = MediaType.APPLICATION_JSON
            queryParameters.forEach {
                param(it.first, it.second)
            }
        }.andReturn().asObject()
    }

    inline fun <reified T>put(url: String, body: Any): T {
        return mockMvc.put(url) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.andReturn().asObject()
    }

    fun delete(url: String) {
        mockMvc.delete(url)
    }

    final inline fun <reified T> MvcResult.asObject(): T {
        return this.response.contentAsString.asObject()
    }

    final inline fun <reified T> String.asObject(): T {
        return objectMapper.readValue(this)
    }
}