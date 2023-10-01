package com.example.nosqlkotlin.user

import com.example.nosqlkotlin.project.Job
import com.example.nosqlkotlin.project.Project
import com.example.nosqlkotlin.project.ResponseStatus
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.bson.Document

@RestController
@RequestMapping("/users")
class UserController(
    val mongoTemplate: MongoTemplate
) {
    @GetMapping("/{id}/jobs/interview")
    fun getInterviewJobs(
        @PathVariable id: String
    ): List<Job> {
        val userId = ObjectId(id)

        val aggregation = newAggregation(
            unwind("jobs"),
            unwind("jobs.responses"),
            match(
                Criteria.where("jobs.responses.status").`is`(ResponseStatus.INTERVIEW)
                .and("jobs.responses.user").`is`(userId))
        )

        val result = mongoTemplate.aggregate(aggregation, "project", Project::class.java)
        val a = result.mappedResults

        return (a as List<Project>).flatMap { it.jobs }
    }
}