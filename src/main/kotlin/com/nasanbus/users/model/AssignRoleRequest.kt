package com.nasanbus.users.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AssignRoleRequest(
    @field:NotBlank
    @field:Size(max = 50)
    val roleCode: String,
)
