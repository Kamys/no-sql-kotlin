package com.example.nosqlkotlin.common.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
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

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleAllExceptions(exception: Exception): ErrorResponse {
        logger.error("Unexpected error occurred", exception)
        return ErrorResponse(
            title = "Internal Server Error",
            details = "An unexpected error occurred. Please try again later."
        )
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleServerError(exception: Throwable): ErrorResponse {
        logger.error(HttpStatus.INTERNAL_SERVER_ERROR.logTitle(), exception)
        return ErrorResponse(title = "Internal server error")
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val response = if (status.is4xxClientError) {
            ErrorResponse(title = ex.message ?: "Invalid request")
        } else {
            ErrorResponse(title = "Internal server error")
        }

        if (status.is5xxServerError) logger.error(status, ex) else logger.warn(status.logTitle(), ex)

        return ResponseEntity(response, headers, status)
    }

    private fun HttpStatus.logTitle() = "${value()} $reasonPhrase"
    private val reasonPhraseMap = HttpStatus.values().associate { Pair(it.value(), it.reasonPhrase) }
    private fun HttpStatusCode.logTitle() = "${value()} ${reasonPhraseMap[value()]}"
}
