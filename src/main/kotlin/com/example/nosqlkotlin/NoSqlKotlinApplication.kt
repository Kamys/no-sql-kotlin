package com.example.nosqlkotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@RestController
class Controller(
    val userRepository: UsersRepository
) {

    @GetMapping("/")
    fun index(): String {
        val user = userRepository.findByName("Nikita")

        if (user == null) {
            return "Not found user"
        }

        return "Success ${user.name} ${user.email}"
    }

}


@Document("users")
class User(
    val name: String?,
    val email: String?,
) {

}

interface UsersRepository : MongoRepository<User, String> {
    fun findByName(primaryKey: String): User?
}