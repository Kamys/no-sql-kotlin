package com.example.nosqlkotlin
import com.example.nosqlkotlin.common.exception.ConflictException
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference
import org.springframework.data.mongodb.repository.MongoRepository

interface ProjectRepository : MongoRepository<Project, String> {
    fun findById(id: ObjectId): Project?
    override fun findAll(pageable: Pageable): Page<Project>
    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Project>
}

@Document("project")
class Project(
    @Id
    val id: ObjectId = ObjectId.get(),
    var name: String,
    var jobs: List<Job> = emptyList()
)

class Job(
    val id: ObjectId = ObjectId.get(),
    var name: String,
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