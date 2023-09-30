package com.example.nosqlkotlin.changelogs

import com.example.nosqlkotlin.*
import io.mongock.api.annotations.*
import org.springframework.data.mongodb.core.MongoTemplate

@ChangeUnit(id = "ProjectMigration", order = "1", systemVersion = "1")
class InitialData(
    private val mongoTemplate: MongoTemplate
) {
    @BeforeExecution
    fun before() {}

    @Execution
    fun migrationMethod() {
        val user = User(name = "Alex", email = "alex@mail.com")
        val job = Job(name = "Кузнец")
        job.addResponse(Response(user = user, status = ResponseStatus.REQUEST))
        val project = Project(
            name = "Project 1", jobs = listOf(
                job
            )
        )
        mongoTemplate.save(project)
        mongoTemplate.save(user)
    }

    @RollbackBeforeExecution
    fun rollbackBefore() {}

    @RollbackExecution
    fun rollback() {}
}