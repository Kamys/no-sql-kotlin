package com.example.nosqlkotlin

import org.bson.types.ObjectId
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    val context = runApplication<Application>(*args)
    //initData(context)

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

    @GetMapping("/{project-name}")
    fun index(@PathVariable("project-name") projectName: String): Any {
        val project = projectsRepository.findByName(projectName)

        if (project == null) {
            return "Not found $projectName"
        }

        return project.jobs
    }

    @GetMapping("/{project-name}/job-responses-cccepted")
    fun getAllResponsesAccepted(@PathVariable("project-name") projectName: String): Any {
        val project = projectsRepository.findByName(projectName)

        if (project == null) {
            return "Not found $projectName"
        }

        return project.jobs
    }

}