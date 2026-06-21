package com.nasanbus.users.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "account_roles", schema = "users")
@IdClass(AccountRoleId::class)
class AccountRoleEntity(
    @Id
    var accountId: UUID,
    @Id
    var roleId: UUID,
    var addedOn: LocalDateTime = LocalDateTime.now(),
)
