package com.example.nosqlkotlin

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

@Document("user")
class User(
    @Id
    val id: ObjectId = ObjectId.get(),
    val name: String,
    val email: String? = null,
)

interface UserRepository : MongoRepository<User, String> {
    fun findByName(primaryKey: String): User?
    fun findById(id: ObjectId): User?
}