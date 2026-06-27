package com.nasanbus.users.repository

import com.nasanbus.users.entity.AccountRoleEntity
import com.nasanbus.users.entity.AccountRoleId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface AccountRoleRepository : JpaRepository<AccountRoleEntity, AccountRoleId> {
    fun findByAccountId(accountId: UUID): List<AccountRoleEntity>

    @Query(
        """
        SELECT r.code
        FROM AccountRoleEntity ar
        JOIN RoleEntity r ON r.id = ar.roleId
        WHERE ar.accountId = :accountId
        ORDER BY r.code
        """,
    )
    fun findRoleCodesByAccountId(
        @Param("accountId") accountId: UUID,
    ): List<String>

    fun existsByAccountIdAndRoleId(
        accountId: UUID,
        roleId: UUID,
    ): Boolean

    @Modifying
    @Query(
        value =
            """
            INSERT INTO users.account_roles (account_id, role_id)
            VALUES (:accountId, :roleId)
            ON CONFLICT (account_id, role_id) DO NOTHING
            """,
        nativeQuery = true,
    )
    fun assignRoleIfAbsent(
        @Param("accountId") accountId: UUID,
        @Param("roleId") roleId: UUID,
    ): Int
}
