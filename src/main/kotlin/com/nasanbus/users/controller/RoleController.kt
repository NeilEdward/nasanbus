package com.nasanbus.users.controller

import com.nasanbus.users.model.Role
import com.nasanbus.users.service.RoleService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/roles")
class RoleController(
    private val roleService: RoleService,
) {
    @GetMapping
    fun getRoles(): List<Role> = roleService.getRoles()

    @GetMapping("/{code}")
    fun getRoleByCode(
        @PathVariable code: String,
    ): Role =
        roleService.getRoleByCode(code)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found")
}
