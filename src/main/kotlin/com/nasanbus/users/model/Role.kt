package com.nasanbus.users.model

import com.nasanbus.common.model.Auditable
import java.time.LocalDateTime
import java.util.UUID

data class Role(
    val id: UUID? = null,
    val code: String,
    val name: String,
    val description: String? = null,
    override val addedOn: LocalDateTime? = null,
    override val addedBy: String,
    override val updatedBy: String,
    override val updatedOn: LocalDateTime? = null,
    override val deletedBy: String? = null,
    override val deletedOn: LocalDateTime? = null,
) : Auditable
