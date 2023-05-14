package com.example.nosqlkotlin

class ProjectResponse(
    val projects: List<Project>,
    val currentPage: Int,
    val totalPages: Int,
    val totalSize: Long
)

class ProjectFilter {
    var limit: Int = 20
    var page: Int = 0
    var searchTerm: String? = null
}