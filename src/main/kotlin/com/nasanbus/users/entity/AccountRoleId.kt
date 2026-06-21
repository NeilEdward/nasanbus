package com.nasanbus.users.entity

import java.io.Serializable
import java.util.UUID

data class AccountRoleId(
    var accountId: UUID? = null,
    var roleId: UUID? = null,
) : Serializable
