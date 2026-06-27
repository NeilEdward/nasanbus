package com.nasanbus.common.exception

import com.nasanbus.users.service.AccountNotFoundException
import com.nasanbus.users.service.RoleNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(
        NotFoundException::class,
        AccountNotFoundException::class,
        RoleNotFoundException::class,
    )
    fun handleNotFound(exception: RuntimeException): ErrorResponse = ErrorResponse(requireNotNull(exception.message))

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(exception: ForbiddenException): ErrorResponse = ErrorResponse(requireNotNull(exception.message))

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException::class)
    fun handleConflict(exception: ConflictException): ErrorResponse = ErrorResponse(requireNotNull(exception.message))
}

data class ErrorResponse(
    val message: String,
)
