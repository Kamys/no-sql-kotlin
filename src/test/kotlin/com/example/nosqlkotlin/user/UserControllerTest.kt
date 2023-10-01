package com.example.nosqlkotlin.user

import com.example.nosqlkotlin.BaseTest
import com.example.nosqlkotlin.project.Job
import com.example.nosqlkotlin.project.Project
import com.example.nosqlkotlin.project.Response
import com.example.nosqlkotlin.project.ResponseStatus
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.bson.types.ObjectId

import org.junit.jupiter.api.Test

class UserControllerTest : BaseTest() {
    @Test
    fun `should get interview jobs for user`() {
        // Arrange
        val userId = ObjectId.get().toHexString()

        val user = User(id = ObjectId(userId), name = "Alex")
        val userOther = User(id = ObjectId(userId), name = "Other user")

        val projectFirst = Project(
            name = "TestProject",
            jobs = listOf(
                Job(
                    name = "Job 1", responses = mutableListOf(
                        Response(user = user, status = ResponseStatus.INTERVIEW)
                    )
                ),
                Job(
                    name = "Job 2", responses = mutableListOf(
                        Response(user = user, status = ResponseStatus.REQUEST)
                    )
                )
            )
        )
        val projectSecond = Project(
            name = "TestProject",
            jobs = listOf(
                Job(
                    name = "Job 3", responses = mutableListOf(
                        Response(user = user, status = ResponseStatus.INTERVIEW)
                    )
                ),
                Job(
                    name = "Job 4", responses = mutableListOf(
                        Response(user = userOther, status = ResponseStatus.INTERVIEW)
                    )
                ),
                Job(
                    name = "Job 5", responses = mutableListOf(
                        Response(user = user, status = ResponseStatus.RESPONSE_ACCEPTED)
                    )
                )
            )
        )

        mongoTemplate.save(projectFirst)
        mongoTemplate.save(projectSecond)

        // Act
        val result: List<Job> = jsonClient.get(
            url = "/users/$userId/jobs/interview",
        )

        // Assert
        result.shouldHaveSize(2)
        result.map { it.name }.shouldContainExactly("Job 1", "Job 3")
        result.flatMap { it.responses }.forEach {
            it.status.shouldBe(ResponseStatus.INTERVIEW)
            it.user.id.shouldBe(ObjectId(userId))
        }
    }
}