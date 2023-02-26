package com.example.nosqlkotlin

import com.example.nosqlkotlin.exception.NotFoundException
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectController(
    val projectRepository: ProjectRepository,
    val userRepository: UserRepository
) {

    @PostMapping("project/{projectId}/job/{jobId}/responses")
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