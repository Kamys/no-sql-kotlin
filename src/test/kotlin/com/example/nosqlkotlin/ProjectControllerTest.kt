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
    fun `should create project`() {
        // Arrange
        val request = ProjectCreateRequest(
            name = "New project name",
            jobs = listOf(
                ProjectCreateRequest.Job(name = "Job 1"),
                ProjectCreateRequest.Job(name = "Job 2"),
            )
        )

        // Act
        val view: Project = postJson(
            url = "/projects",
            body = request
        )

        // Assert
        val newProject = projectRepository.findById(view.id).shouldNotBeNull()
        newProject.name.shouldBe(request.name)
        newProject.jobs.shouldHaveSize(2)
        newProject.jobs.map { it.name }.shouldContainExactly("Job 1", "Job 2")

        view.name.shouldBe(request.name)
        view.jobs.shouldHaveSize(2)
        view.jobs.map { it.name }.shouldContainExactly("Job 1", "Job 2")
    }

    @Test
    fun `should update project`() {
        // Arrange
        val projectId = ObjectId.get()
        val jobIdFirst = ObjectId.get()
        val jobIdSecond = ObjectId.get()
        val projectForUpdate = Project(
            id = projectId,
            name = "Project old name",
            jobs = listOf(
                Job(id = jobIdFirst, name = "First job old name"),
                Job(id = jobIdSecond, name = "Second job old name")
            )
        )
        projectRepository.save(projectForUpdate)

        val request = ProjectUpdateRequest(
            name = "New project name",
            jobs = listOf(
                ProjectUpdateRequest.Job(id = jobIdFirst, name = "New job name 1"),
                ProjectUpdateRequest.Job(id = jobIdSecond, name = "New job name 2"),
            )
        )

        // Act
        val view: Project = putJson(
            url = "/projects/${projectId}",
            body = request
        )

        // Assert
        val newProject = projectRepository.findById(projectId).shouldNotBeNull()
        newProject.name.shouldBe(request.name)
        newProject.jobs.shouldHaveSize(2)
        newProject.jobs.map { it.name }.shouldContainExactly(request.jobs[0].name, request.jobs[1].name)

        view.name.shouldBe(request.name)
        view.jobs.shouldHaveSize(2)
        view.jobs.map { it.name }.shouldContainExactly(request.jobs[0].name, request.jobs[1].name)
    }

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