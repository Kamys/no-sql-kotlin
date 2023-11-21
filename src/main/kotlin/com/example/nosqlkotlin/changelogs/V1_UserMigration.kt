package com.example.nosqlkotlin.changelogs

import com.example.nosqlkotlin.project.*
import com.example.nosqlkotlin.user.User
import com.example.nosqlkotlin.user.UserRepository
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution

@ChangeUnit(id = "UserMigration", order = "1", systemVersion = "1")
class V1_UserMigration(
    private val userRepository: UserRepository,
    private val projectsRepository: ProjectRepository,
) {

    @Execution
    fun migrationMethod() {
        val alex = User(name = "Alex", email = "alex@mail.com")
        val bella = User(name = "Bella", email = "bella@mail.com")
        val charlie = User(name = "Charlie", email = "charlie@mail.com")

        userRepository.save(alex)
        userRepository.save(bella)
        userRepository.save(charlie)
    }

    /**
     * If the ChangeUnit fails, the runner rolls back the change.
     * If the driver supports transactions and transactions are enabled, the rollback is done natively.
     * When the driver does not support transactions or transactions are disabled,
     * the method @RollbackExecution is executed.
     */
    @RollbackExecution
    fun rollback() {
    }
}