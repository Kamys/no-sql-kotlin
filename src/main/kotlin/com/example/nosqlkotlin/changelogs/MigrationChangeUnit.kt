package com.example.nosqlkotlin.changelogs

import com.example.nosqlkotlin.*
import io.mongock.api.annotations.*
import org.springframework.data.mongodb.core.MongoTemplate

@ChangeUnit(id = "ProjectMigration", order = "1", systemVersion = "1")
class MyMigrationChangeUnit(
    private val mongoTemplate: MongoTemplate
) {

    @BeforeExecution
    fun before() {}


    @RollbackBeforeExecution
    fun rollbackBefore() {}

    @Execution
    fun migrationMethod() {
        val user = User(name = "Alex", email = "alex@mail.com")
        val responses = listOf(Response(user = user, status = ResponseStatus.REQUEST))
        val project = Project(
            name = "Project 1", jobs = listOf(
                Job("Кузнец", responses = responses)
            )
        )
        mongoTemplate.save(project)
    }

    @RollbackExecution
    fun rollback() {}
}