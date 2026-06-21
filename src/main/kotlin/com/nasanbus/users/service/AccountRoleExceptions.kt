package com.nasanbus.users.service

import java.util.UUID

class AccountNotFoundException(
    accountId: UUID,
) : RuntimeException("Account $accountId not found")

class RoleNotFoundException(
    roleCode: String,
) : RuntimeException("Role $roleCode not found")
