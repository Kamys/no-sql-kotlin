package com.example.nosqlkotlin

import io.mockk.every
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post


class ProjectControllerTest: BaseTest() {

    @Test
    fun `sendResponse should add response to the job and return the updated project`() {
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

        every { projectRepository.findById(projectId) } returns project
        every { userRepository.findById(any<ObjectId>()) } returns user
        every { projectRepository.save(project) } returns project

        // Act
        val result = mockMvc.post("/project/$projectId/job/$jobId/responses") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(responseRequest)
        }.andReturn()

        // Assert
        val updatedProject = result.asObject<Project>()
        assert(updatedProject.jobs[0].responses.size == 1)
        assert(updatedProject.jobs[0].responses[0].user.id.toString() == user.id.toString())
        assert(updatedProject.jobs[0].responses[0].status == ResponseStatus.REQUEST)
    }
}


