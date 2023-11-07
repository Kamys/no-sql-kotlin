package com.example.nosqlkotlin

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.support.NoOpCacheManager
import org.springframework.context.annotation.*
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.*
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
@Profile("test")
class CacheTestConfig {
    @Primary
    @Bean
    fun cacheManager(): CacheManager {
        return NoOpCacheManager()
    }
}

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class BaseTest {
    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var jsonClient: JsonClient

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
}