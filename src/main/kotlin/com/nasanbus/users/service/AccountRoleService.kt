package com.nasanbus.users.service

import com.nasanbus.users.entity.toData
import com.nasanbus.users.model.AccountRoleResponse
import com.nasanbus.users.model.toAccountRoleResponse
import com.nasanbus.users.repository.AccountRepository
import com.nasanbus.users.repository.AccountRoleRepository
import com.nasanbus.users.repository.RoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Locale
import java.util.UUID

@Service
class AccountRoleService(
    private val accountRepository: AccountRepository,
    private val roleRepository: RoleRepository,
    private val accountRoleRepository: AccountRoleRepository,
) {
    @Transactional
    fun assignRoleToAccount(
        accountId: UUID,
        roleCode: String,
    ): AccountRoleResponse {
        requireAccount(accountId)

        val normalizedRoleCode = roleCode.trim().uppercase(Locale.ROOT)
        val role =
            roleRepository.findByCode(normalizedRoleCode)
                ?: throw RoleNotFoundException(normalizedRoleCode)
        val roleId = requireNotNull(role.id)

        accountRoleRepository.assignRoleIfAbsent(accountId, roleId)

        return role.toData().toAccountRoleResponse()
    }

    @Transactional(readOnly = true)
    fun getAccountRoles(accountId: UUID): List<AccountRoleResponse> {
        requireAccount(accountId)

        val assignments = accountRoleRepository.findByAccountId(accountId)
        val rolesById =
            roleRepository
                .findAllById(assignments.map { it.roleId })
                .associateBy { requireNotNull(it.id) }

        return assignments
            .map { assignment ->
                rolesById.getValue(assignment.roleId).toData().toAccountRoleResponse()
            }.sortedBy(AccountRoleResponse::code)
    }

    private fun requireAccount(accountId: UUID) {
        if (!accountRepository.existsById(accountId)) {
            throw AccountNotFoundException(accountId)
        }
    }
}
