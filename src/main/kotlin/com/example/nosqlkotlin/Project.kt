package com.example.nosqlkotlin

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
    val jobs: List<Job>
)

class Job(
    val id: ObjectId = ObjectId.get(),
    val name: String,
    var responses: List<Response>,
)

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