package com.nasanbus.users.controller

import com.nasanbus.users.model.CurrentUserResponse
import com.nasanbus.users.service.AccountService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class CurrentUserController(
    private val accountService: AccountService,
) {
    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal jwt: Jwt,
    ): CurrentUserResponse = accountService.getCurrentUser(jwt.subject)
}
