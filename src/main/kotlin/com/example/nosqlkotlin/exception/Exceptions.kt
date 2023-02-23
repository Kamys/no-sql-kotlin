package com.example.nosqlkotlin.exception

open class BaseException : RuntimeException {
    final override val message: String

    constructor(message: String) : super(message) {
        this.message = message
    }

    constructor(message: String, cause: Throwable) : super(message, cause) {
        this.message = message
    }
}

class NotFoundException(message: String) : BaseException(message)
class ConflictException(message: String) : BaseException(message)

class ErrorResponse(
    val title: String,
    val details: String? = null,
)