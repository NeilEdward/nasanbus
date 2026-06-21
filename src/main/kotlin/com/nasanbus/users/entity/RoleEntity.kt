package com.nasanbus.users.entity

import com.nasanbus.users.model.Role
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "roles", schema = "users")
class RoleEntity(
    @Id
    var id: UUID? = null,
    var code: String,
    var name: String,
    var description: String? = null,
    var addedOn: LocalDateTime? = null,
    var addedBy: String,
    var updatedBy: String,
    var updatedOn: LocalDateTime? = null,
    var deletedBy: String? = null,
    var deletedOn: LocalDateTime? = null,
)

internal fun RoleEntity.toData() =
    Role(
        id = id,
        code = code,
        name = name,
        description = description,
        addedOn = addedOn,
        addedBy = addedBy,
        updatedBy = updatedBy,
        updatedOn = updatedOn,
        deletedBy = deletedBy,
        deletedOn = deletedOn,
    )
