package com.example.nosqlkotlin

import io.mongock.runner.springboot.EnableMongock
import jakarta.annotation.PostConstruct
import org.bson.types.ObjectId
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component

@Component
class MongoDataInitializer(
    private val mongoTemplate: MongoTemplate
) {

    @PostConstruct
    fun clearDatabase() {
        //mongoTemplate.db.drop()
    }
}

@EnableMongock
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

fun initData(context: ConfigurableApplicationContext) {
    val userRepository = context.getBean(UserRepository::class.java)
    val projectsRepository = context.getBean(ProjectRepository::class.java)
    userRepository.save(User(id = ObjectId.get(), name = "Ron", email = "ron@mail.com"))


    val user = userRepository.findByName("Alex")!!
    val job = Job(
        name = "Job 4",
    )
    job.addResponse(
        Response(
            user = user,
            status = ResponseStatus.REQUEST
        )
    )
    job.addResponse(
        Response(
            user = user,
            status = ResponseStatus.INTERVIEW
        )
    )
    val project = Project(
        name = "Project 4",
        jobs = listOf(job)
    )
    projectsRepository.insert(project)
}

class JobResponseCreateRequest(
    val userId: ObjectId
)

