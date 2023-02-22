package com.example.nosqlkotlin

import io.mongock.runner.springboot.EnableMongock
import org.bson.types.ObjectId
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.web.bind.annotation.*
import java.util.*

@EnableMongock
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    val context = runApplication<Application>(*args)
    //initData(context) SpringDataMongoV3Driver

}

fun initData(context: ConfigurableApplicationContext) {
    val userRepository = context.getBean(UsersRepository::class.java)
    val projectsRepository = context.getBean(ProjectRepository::class.java)
    userRepository.save(User(id = ObjectId.get(), name = "Ron", email = "ron@mail.com"))


    val user = userRepository.findByName("Alex")!!
    val job = Job(
        name = "Job 4",
        responses = listOf(
            Response(
                user = user,
                status = ResponseStatus.REQUEST
            ),
            Response(
                user = user,
                status = ResponseStatus.INTERVIEW
            )
        )
    )
    val project = Project(
        name = "Project 4",
        jobs = listOf(job)
    )
    projectsRepository.insert(project)
}

@RestController
class Controller(
    val projectsRepository: ProjectRepository,
    val userRepository: UsersRepository
) {

    class ResponseRequest(
        val userId: ObjectId
    )

    @PostMapping("project/{projectId}/job/{jobID}")
    fun sendResponse(projectId: ObjectId, jobId: ObjectId, @RequestBody responseRequest: ResponseRequest): String {
        val project = projectsRepository.findById(projectId)

        if (project == null) {
            return "Not found project $projectId"
        }

        val job = project.jobs.find { it.id == jobId }

        if (job == null) {
            return "Not found job $jobId"
        }

        val user = userRepository.findById(responseRequest.userId)

        if (user == null) {
            return "Not found user ${responseRequest.userId}"
        }

        job.responses = job.responses + Response(user = user, status = ResponseStatus.REQUEST)

        projectsRepository.save(project)

        return "Success!"
    }
}