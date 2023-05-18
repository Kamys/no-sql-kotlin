package com.example.nosqlkotlin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.*
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan("com.example.nosqlkotlin")
class BaseTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun clearAllTable() {
        val query = Query().apply {
            addCriteria(Criteria())
        }
        val ignoreCollectionNames = listOf("mongockLock", "mongockChangeLog")
        for (collectionName in mongoTemplate.collectionNames) {
            if (!ignoreCollectionNames.contains(collectionName)) {
                mongoTemplate.remove(query, collectionName)
            }
        }
    }

    protected final inline fun <reified T>postJson(url: String, body: Any): T {
        return mockMvc.post(url) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.andReturn().asObject()
    }

    protected final inline fun <reified T>putJson(url: String, body: Any): T {
        return mockMvc.put(url) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }.andReturn().asObject()
    }


    protected final inline fun <reified T>getJson(url: String, body: Any): T {
        val resultActionsDsl = mockMvc.get(url) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(body)
        }
        val result = resultActionsDsl.andReturn()
        return result.asObject()
    }

    companion object {
        private val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:4.0.10")).also {
            it.withTmpFs(mapOf("/var/lib/mongodb/data" to "rw"))
            it.withReuse(true)
            it.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun datasourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.host", mongoDBContainer::getHost)
            registry.add("spring.data.mongodb.port") { mongoDBContainer.firstMappedPort }
            registry.add("spring.data.mongodb.authentication-database") { "admin" }
        }
    }

    final inline fun <reified T> MvcResult.asObject(): T {
        return this.response.contentAsString.asObject()
    }

    final inline fun <reified T> String.asObject(): T {
        return objectMapper.readValue(this)
    }
}