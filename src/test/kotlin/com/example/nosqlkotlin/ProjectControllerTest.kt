package com.example.nosqlkotlin

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate

class ProjectControllerTest(
    @Autowired
    private val projectRepository: ProjectRepository,
) : BaseTest() {

    @Test
    fun `should add response in job and return updated project`() {
        // Arrange
        val projectId = ObjectId.get()
        val jobId = ObjectId.get()
        val user = User(id = ObjectId.get(), name = "Test user")
        val request = JobResponseCreateRequest(userId = user.id)
        val project = Project(
            id = projectId,
            name = "Test project",
            jobs = listOf(Job(id = jobId, name = "Test job", responses = mutableListOf()))
        )

        mongoTemplate.save(project)
        mongoTemplate.save(user)

        // Act
        val view: Project = postJson(
            url = "/projects/$projectId/job/$jobId/responses",
            body = request
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

    @Test
    fun `should get projects`() {
        // Arrange
        val request = ProjectFilter().apply {
            this.limit = 3
            this.page = 0
            this.searchTerm = "data"
        }

        val projectForFirstPage = listOf(
            Project( name = "Project_data" ),
            Project( name = "Project Data" ),
            Project( name = "SDataProject" ),
        )

        val otherProject = listOf(
            Project( name = "Other project 1" ),
            Project( name = "Other project 2" ),
        )

        (projectForFirstPage + otherProject).forEach {
            mongoTemplate.save(it)
        }

        // Act
        val view: ProjectResponse = getJson(
            url = "/projects",
            body = request
        )

        // Assert
        view.totalPages.shouldBe(1)
        view.currentPage.shouldBe(0)
        view.totalSize.shouldBe(3)
        view.projects.shouldHaveSize(3)
        view.projects.map { it.id }.shouldContainExactly(projectForFirstPage.map { it.id })
    }
}