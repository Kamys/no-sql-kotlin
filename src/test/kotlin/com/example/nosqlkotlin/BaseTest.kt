package com.example.nosqlkotlin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult

@ExtendWith(SpringExtension::class)
@WebMvcTest(ProjectController::class)
@TestPropertySource(properties = ["mongock.enabled=false"])
open class BaseTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var projectRepository: ProjectRepository

    @MockkBean
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {}

    inline fun <reified T> MvcResult.asObject(): T {
        return this.response.contentAsString.asObject()
    }

    inline fun <reified T> String.asObject(): T {
        return objectMapper.readValue(this)
    }
}