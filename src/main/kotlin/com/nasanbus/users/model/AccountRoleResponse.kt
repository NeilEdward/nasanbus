package com.nasanbus.users.model

data class AccountRoleResponse(
    val code: String,
    val name: String,
)

internal fun Role.toAccountRoleResponse() =
    AccountRoleResponse(
        code = code,
        name = name,
    )
