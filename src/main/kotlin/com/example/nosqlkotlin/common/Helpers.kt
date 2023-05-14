package com.example.nosqlkotlin.common

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

abstract class WithObjectId(
    @Id
    val id: ObjectId = ObjectId.get()
)