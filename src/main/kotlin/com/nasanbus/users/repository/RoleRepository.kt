package com.nasanbus.users.repository

import com.nasanbus.users.entity.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RoleRepository : JpaRepository<RoleEntity, UUID> {
    fun findByCode(code: String): RoleEntity?

    fun existsByCode(code: String): Boolean
}
