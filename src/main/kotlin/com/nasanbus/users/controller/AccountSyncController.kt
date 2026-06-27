package com.nasanbus.users.controller

import com.nasanbus.auth.CognitoClaimExtractor
import com.nasanbus.users.model.SyncAccountResponse
import com.nasanbus.users.service.AccountService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/accounts")
class AccountSyncController(
    private val accountService: AccountService,
    private val cognitoClaimExtractor: CognitoClaimExtractor,
) {
    @PostMapping("/sync")
    fun syncAccount(
        @AuthenticationPrincipal jwt: Jwt,
    ): SyncAccountResponse {
        val claims = cognitoClaimExtractor.extract(jwt)

        return accountService.syncAccountFromCognito(claims)
    }
}
