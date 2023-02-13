package com.example.nosqlkotlin

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference
import org.springframework.data.mongodb.repository.MongoRepository

interface ProjectRepository : MongoRepository<Project, String> {
    fun findByName(primaryKey: String): Project?
}

@Document("project")
class Project(
    val name: String,
    val jobs: List<Job>
)

class Job(
    val name: String,
    val responses: List<Response>,
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