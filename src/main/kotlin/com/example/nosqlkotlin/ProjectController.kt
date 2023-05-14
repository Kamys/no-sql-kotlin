package com.example.nosqlkotlin

import com.example.nosqlkotlin.common.exception.NotFoundException
import org.bson.types.ObjectId
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectRepository: ProjectRepository,
    val userRepository: UserRepository
) {
    @GetMapping
    fun getProjects(
        @RequestBody filter: ProjectFilter
    ): ProjectResponse {
        val paging = PageRequest.of(filter.page, filter.limit)
        val page = projectRepository.smartSearch(filter.searchTerm, paging)

        return ProjectResponse(
            projects = page.content,
            currentPage = page.number,
            totalPages = page.totalPages,
            totalSize = page.totalElements,
        )
    }

    private fun ProjectRepository.smartSearch(name: String?, pageable: Pageable): Page<Project> {
        if (name.isNullOrEmpty()) {
            return findAll(pageable)
        }

        return findByNameContainingIgnoreCase(name, pageable)
    }

    @PostMapping("/{projectId}/job/{jobId}/responses")
    fun sendResponse(
        @PathVariable projectId: ObjectId,
        @PathVariable jobId: ObjectId,
        @RequestBody responseRequest: ResponseRequest
    ): Project {
        val project = projectRepository.findById(projectId)

        if (project == null) {
            throw NotFoundException("Not found project $projectId")
        }

        val job = project.jobs.find { it.id == jobId }

        if (job == null) {
            throw NotFoundException("Not found job $jobId")
        }

        val user = userRepository.findById(responseRequest.userId)

        if (user == null) {
            throw NotFoundException("Not found user ${responseRequest.userId}")
        }

        job.addResponse(Response(user = user, status = ResponseStatus.REQUEST))

        projectRepository.save(project)

        return project
    }
}