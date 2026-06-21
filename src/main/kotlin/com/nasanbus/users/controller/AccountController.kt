package com.nasanbus.users.controller

import com.nasanbus.users.model.Account
import com.nasanbus.users.model.CreateAccountRequest
import com.nasanbus.users.model.toData
import com.nasanbus.users.service.AccountService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@RestController
@RequestMapping("/api/v1/accounts")
class AccountController(
    private val accountService: AccountService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateAccountRequest,
    ): Account = accountService.create(request.toData())

    @GetMapping
    fun findAll(): List<Account> = accountService.findAll()

    @GetMapping("/{id}")
    fun findById(
        @PathVariable id: UUID,
    ): Account =
        accountService.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")
}
