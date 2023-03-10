package com.example.nosqlkotlin

import com.example.nosqlkotlin.exception.ConflictException
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference
import org.springframework.data.mongodb.repository.MongoRepository

interface ProjectRepository : MongoRepository<Project, String> {
    fun findById(id: ObjectId): Project?
}

@Document("project")
class Project(
    @Id
    val id: ObjectId = ObjectId.get(),
    val name: String,
    val jobs: List<Job> = emptyList()
)

class Job(
    val id: ObjectId = ObjectId.get(),
    val name: String,
    var responses: MutableList<Response> = mutableListOf(),
) {
    fun addResponse(response: Response) {
        if (responses.any { it.user.id == response.user.id }) {
            throw ConflictException("User has already submitted a response for this job")
        }
        responses.add(response)
    }
}

class Response(
    @DocumentReference
    val user: User,
    val status: ResponseStatus
)

enum class ResponseStatus {
    REQUEST,
    INTERVIEW,
    RESPONSE_ACCEPTED,
}