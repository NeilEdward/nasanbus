package com.nasanbus.users.controller

import com.nasanbus.users.model.AccountRoleResponse
import com.nasanbus.users.model.AssignRoleRequest
import com.nasanbus.users.service.AccountRoleService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/accounts/{accountId}/roles")
class AccountRoleController(
    private val accountRoleService: AccountRoleService,
) {
    @PostMapping
    fun assignRoleToAccount(
        @PathVariable accountId: UUID,
        @Valid @RequestBody request: AssignRoleRequest,
    ): AccountRoleResponse = accountRoleService.assignRoleToAccount(accountId, request.roleCode)

    @GetMapping
    fun getAccountRoles(
        @PathVariable accountId: UUID,
    ): List<AccountRoleResponse> = accountRoleService.getAccountRoles(accountId)
}
