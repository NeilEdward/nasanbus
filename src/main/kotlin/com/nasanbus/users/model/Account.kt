package com.nasanbus.users.model

import com.nasanbus.common.model.Auditable
import java.time.LocalDateTime
import java.util.UUID

data class Account(
    val id: UUID? = null,
    val cognitoSub: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    override val addedOn: LocalDateTime? = null,
    override val addedBy: String,
    override val updatedBy: String,
    override val updatedOn: LocalDateTime? = null,
    override val deletedBy: String? = null,
    override val deletedOn: LocalDateTime? = null,
) : Auditable
