package com.nasanbus.users.controller

import com.nasanbus.common.exception.ConflictException
import com.nasanbus.users.service.AccountNotFoundException
import com.nasanbus.users.service.RoleNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AccountNotFoundException::class, RoleNotFoundException::class)
    fun handleNotFound(exception: RuntimeException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            requireNotNull(exception.message),
        )

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException::class)
    fun handleConflict(exception: ConflictException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            requireNotNull(exception.message),
        )
}
