package com.example.nosqlkotlin.project

import com.example.nosqlkotlin.user.UserRepository
import com.example.nosqlkotlin.common.exception.NotFoundException
import org.bson.types.ObjectId
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectRepository: ProjectRepository,
    val userRepository: UserRepository,
) {

    @PostMapping
    @CacheEvict("project_search", allEntries = true)
    fun createProject(
        @RequestBody request: ProjectCreateRequest,
    ): Project {
        val project = Project(
            name = request.name,
            jobs = request.jobs.map { Job(name = it.name) }
        )
        projectRepository.save(project)

        return project
    }

    @GetMapping
    @Cacheable("project_search", key = "{#filter.limit, #filter.page, #filter.searchTerm}")
    fun getProjects(
        filter: ProjectFilter,
    ): ProjectResponse {
        val page = projectRepository.smartSearch(
            name = filter.searchTerm,
            pageable = PageRequest.of(filter.page, filter.limit)
        )

        return ProjectResponse(
            projects = page.content,
            currentPage = page.number,
            totalPages = page.totalPages,
            totalSize = page.totalElements,
        )
    }

    @GetMapping("/{projectId}")
    @Cacheable("project", key = "#projectId")
    fun getProject(
        @PathVariable projectId: ObjectId,
    ): Project {
        return projectRepository.findById(projectId)
            ?: throw NotFoundException("Not found project $projectId")
    }

    @PutMapping("/{projectId}")
    @Caching(
        put = [CachePut("project", key = "#projectId")],
        evict = [CacheEvict("project_search", allEntries = true)]
    )
    fun updateProject(
        @PathVariable projectId: ObjectId,
        @RequestBody request: ProjectUpdateRequest,
    ): Project {
        val project = projectRepository.findById(projectId)
            ?: throw NotFoundException("Not found project $projectId")
        project.apply {
            this.name = request.name
        }

        project.updateJobs(request)

        projectRepository.save(project)
        return project
    }

    @DeleteMapping("/{projectId}")
    @Caching(
        evict = [
            CacheEvict("project_search", allEntries = true),
            CacheEvict("project", key = "#projectId")
        ]
    )
    fun deleteProject(
        @PathVariable projectId: ObjectId,
    ) {
        val project = projectRepository.findById(projectId)
            ?: throw NotFoundException("Not found project $projectId")
        projectRepository.delete(project)
    }

    private fun Project.updateJobs(request: ProjectUpdateRequest) {
        val updatedJobs = this.jobs.mapNotNull { existingJob ->
            request.jobs.find { it.id == existingJob.id }?.let { newJob ->
                existingJob.name = newJob.name
                existingJob
            }
        }

        val newJobs = request.jobs.filter { newJob ->
            this.jobs.none { existingJob -> existingJob.id == newJob.id }
        }
            .map { Job(name = it.name) }

        this.jobs = updatedJobs + newJobs
    }

    @DeleteMapping("/{projectId}/jobs/{jobId}")
    @Caching(
        evict = [
            CacheEvict("project_search", allEntries = true),
            CacheEvict("project", key = "#projectId")
        ]
    )
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

    private fun ProjectRepository.smartSearch(name: String?, pageable: Pageable): Page<Project> {
        if (name.isNullOrEmpty()) {
            return findAll(pageable)
        }

        return findByNameContainingIgnoreCase(name, pageable)
    }

    @PostMapping("/{projectId}/job/{jobId}/responses")
    @Caching(
        evict = [
            CacheEvict("project_search", allEntries = true),
            CacheEvict("project", key = "#projectId")
        ]
    )
    fun sendResponse(
        @PathVariable projectId: ObjectId,
        @PathVariable jobId: ObjectId,
        @RequestBody responseRequest: JobResponseCreateRequest,
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