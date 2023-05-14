package com.example.nosqlkotlin.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ControllerAdvice : ResponseEntityExceptionHandler() {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundException(exception: NotFoundException): ErrorResponse {
        logger.warn(HttpStatus.NOT_FOUND.logTitle(), exception)
        return ErrorResponse(
            title = "Not found",
            details = exception.message
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleConflictException(exception: ConflictException): ErrorResponse {
        logger.warn(HttpStatus.NOT_FOUND.logTitle(), exception)
        return ErrorResponse(
            title = "Request conflict",
            details = exception.message
        )
    }

    private fun HttpStatus.logTitle() = "${value()} $reasonPhrase"
}
