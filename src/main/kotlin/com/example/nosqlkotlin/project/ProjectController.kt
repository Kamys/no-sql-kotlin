package com.example.nosqlkotlin.project

import com.example.nosqlkotlin.JobResponseCreateRequest
import com.example.nosqlkotlin.user.UserRepository
import com.example.nosqlkotlin.common.exception.NotFoundException
import org.bson.types.ObjectId
import org.springframework.cache.annotation.Cacheable
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

    @PostMapping
    fun createProjects(
        @RequestBody request: ProjectCreateRequest
    ): Project {
        val project = Project(
            name = request.name,
            jobs = request.jobs.map { Job(name = it.name) }
        )
        projectRepository.save(project)

        return project
    }

    @PutMapping("/{projectId}")
    fun updateProjects(
        @PathVariable projectId: ObjectId,
        @RequestBody request: ProjectUpdateRequest,
    ): Project {
        val project = projectRepository.findById(projectId)
            ?: throw NotFoundException("Not found project $projectId")

        project.apply {
            this.name = request.name
        }

        project.jobs.forEach { job ->
            val newJob = request.jobs.find { it.id == job.id }
            if (newJob != null) {
                job.name = newJob.name
            }
        }

        projectRepository.save(project)

        return project
    }

    @DeleteMapping("/{projectId}")
    fun deleteProject(
        @PathVariable projectId: ObjectId,
    ) {
        val project = projectRepository.findById(projectId)
            ?: throw NotFoundException("Not found project $projectId")
        projectRepository.delete(project)
    }

    @DeleteMapping("/{projectId}/jobs/{jobId}")
    fun deleteProjectJob(
        @PathVariable projectId: ObjectId,
        @PathVariable jobId: ObjectId,
    ) {
        val project = projectRepository.findById(projectId)
            ?: throw NotFoundException("Not found project $projectId")
        val jobIndex = project.jobs.indexOfFirst { it.id != jobId }
        if (jobIndex == -1) {
            throw NotFoundException("Not found job $jobId")
        }
        project.jobs = project.jobs.drop(jobIndex)
        projectRepository.save(project)
    }

    @GetMapping
    @Cacheable("projects", key = "{#filter.limit, #filter.page, #filter.searchTerm}")
    fun getProjects(
        @ModelAttribute filter: ProjectFilter
    ): ProjectResponse {
        println("Get project from DB...")
        Thread.sleep(4_000)
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
        @RequestBody responseRequest: JobResponseCreateRequest
    ): Project {
        val project = projectRepository.findById(projectId)
            ?: throw NotFoundException("Not found project $projectId")

        val job = project.jobs.find { it.id == jobId }
            ?: throw NotFoundException("Not found job $jobId")

        val user = userRepository.findById(responseRequest.userId)
            ?: throw NotFoundException("Not found user ${responseRequest.userId}")

        job.addResponse(Response(user = user, status = ResponseStatus.REQUEST))

        projectRepository.save(project)

        return project
    }
}