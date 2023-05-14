package com.example.nosqlkotlin

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

class ProjectControllerTest(
    @Autowired
    private val mongoTemplate: MongoTemplate,
    @Autowired
    private val projectRepository: ProjectRepository,
) : BaseTest() {

    @Test
    fun `should add response in job and return updated project`() {
        // Arrange
        val projectId = ObjectId.get()
        val jobId = ObjectId.get()
        val user = User(id = ObjectId.get(), name = "Test user")
        val responseRequest = ResponseRequest(userId = user.id)
        val project = Project(
            id = projectId,
            name = "Test project",
            jobs = listOf(Job(id = jobId, name = "Test job", responses = mutableListOf()))
        )

        mongoTemplate.save(project)
        mongoTemplate.save(user)

        // Act
        val view: Project = postJson(
            url = "/project/$projectId/job/$jobId/responses",
            body = responseRequest
        )

        // Assert
        view.jobs.ensureFirst().responses.ensureFirst().also {
            it.user.id.shouldBe(user.id)
            it.status.shouldBe(ResponseStatus.REQUEST)
        }

        val updateProject = projectRepository.findById(project.id).shouldNotBeNull()
        updateProject.jobs.ensureFirst().responses.ensureFirst().also {
            it.user.id.shouldBe(user.id)
            it.status.shouldBe(ResponseStatus.REQUEST)
        }
    }
}


