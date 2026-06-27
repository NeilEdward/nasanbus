package com.nasanbus.users.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "accounts", schema = "users")
class AccountEntity(
    @Id
    var id: UUID? = null,
    var cognitoSub: String,
    var email: String,
    var firstName: String? = null,
    var lastName: String? = null,
    var phoneNumber: String? = null,
    var status: String = "ACTIVE",
    var addedOn: LocalDateTime? = null,
    var addedBy: String,
    var updatedBy: String,
    var updatedOn: LocalDateTime? = null,
    var deletedBy: String? = null,
    var deletedOn: LocalDateTime? = null,
)
