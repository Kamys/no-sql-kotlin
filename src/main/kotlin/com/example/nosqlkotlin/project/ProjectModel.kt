package com.example.nosqlkotlin.project

import org.bson.types.ObjectId

class ProjectResponse(
    val projects: List<Project>,
    val currentPage: Int,
    val totalPages: Int,
    val totalSize: Long
)

class ProjectCreateRequest(
    val name: String,
    val jobs: List<Job>
) {
    class Job(
        val name: String,
    )
}

class ProjectUpdateRequest(
    val name: String,
    val jobs: List<Job>
) {
    class Job(
        val id: ObjectId,
        val name: String,
    )
}

class ProjectFilter {
    var limit: Int = 20
    var page: Int = 0
    var searchTerm: String? = null
}