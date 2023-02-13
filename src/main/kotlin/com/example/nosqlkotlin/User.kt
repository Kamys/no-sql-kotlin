package com.example.nosqlkotlin

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository

@Document("user")
class User(
    @Id
    val id: ObjectId,
    val name: String?,
    val email: String?,
) {

}

interface UsersRepository : MongoRepository<User, String> {
    fun findByName(primaryKey: String): User?
}