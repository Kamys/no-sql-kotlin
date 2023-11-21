package com.example.nosqlkotlin.changelogs

import com.example.nosqlkotlin.project.*
import com.example.nosqlkotlin.user.User
import com.example.nosqlkotlin.user.UserRepository
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution

@ChangeUnit(id = "ProjectMigration", order = "2", systemVersion = "1")
class V2_ProjectMigration(
    private val userRepository: UserRepository,
    private val projectsRepository: ProjectRepository,
) {

    @Execution
    fun migrationMethod() {
        val alex = userRepository.findByName("Alex")
        val bella = userRepository.findByName("Bella")
        val charlie = userRepository.findByName("Charlie")

        val alphaProject = Project(
            name = "AlphaProject",
            jobs = listOf(
                Job(
                    name = "Developer",
                    responses = mutableListOf(
                        Response(user = alex, status = ResponseStatus.REQUEST),
                        Response(user = charlie, status = ResponseStatus.REQUEST)
                    )
                ),
                Job(
                    name = "Tester",
                    responses = mutableListOf(
                        Response(user = alex, status = ResponseStatus.REQUEST),
                        Response(user = charlie, status = ResponseStatus.INTERVIEW)
                    )
                )
            )
        )
        projectsRepository.insert(alphaProject)

        val betaProject = Project(
            name = "BetaProject",
            jobs = listOf(
                Job(
                    name = "Designer",
                    responses = mutableListOf(
                        Response(user = bella, status = ResponseStatus.INTERVIEW),
                        Response(user = alex, status = ResponseStatus.INTERVIEW)
                    )
                )
            )
        )
        projectsRepository.insert(betaProject)

        val gammaProject = Project(
            name = "GammaProject",
            jobs = listOf(
                Job(
                    name = "Manager",
                    responses = mutableListOf(
                        Response(user = alex, status = ResponseStatus.REQUEST),
                        Response(user = charlie, status = ResponseStatus.REQUEST),
                        Response(user = bella, status = ResponseStatus.REQUEST),
                    )
                ),
                Job(
                    name = "Accountant",
                    responses = mutableListOf(
                        Response(user = bella, status = ResponseStatus.RESPONSE_ACCEPTED)
                    )
                )
            )
        )
        projectsRepository.insert(gammaProject)
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