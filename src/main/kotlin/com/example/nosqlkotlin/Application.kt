package com.example.nosqlkotlin

import io.mongock.runner.springboot.EnableMongock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableMongock
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

