package com.nasanbus.users.service

import com.nasanbus.users.entity.toData
import com.nasanbus.users.model.AccountRoleResponse
import com.nasanbus.users.model.toAccountRoleResponse
import com.nasanbus.users.repository.AccountRepository
import com.nasanbus.users.repository.AccountRoleRepository
import com.nasanbus.users.repository.RoleRepository
import org.slf4j.LoggerFactory
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
    private companion object {
        private val logger = LoggerFactory.getLogger(AccountRoleService::class.java)
    }

    @Transactional
    fun assignRoleToAccount(
        accountId: UUID,
        roleCode: String,
    ): AccountRoleResponse {
        requireAccount(accountId)

        val normalizedRoleCode = roleCode.trim().uppercase(Locale.ROOT)
        val role =
            roleRepository.findByCode(normalizedRoleCode)
                ?: run {
                    logger.warn(
                        "Rejected role assignment for accountId={} because roleCode={} was not found",
                        accountId,
                        normalizedRoleCode,
                    )
                    throw RoleNotFoundException(normalizedRoleCode)
                }
        val roleId = requireNotNull(role.id)

        val insertedRows = accountRoleRepository.assignRoleIfAbsent(accountId, roleId)
        if (insertedRows == 0) {
            logger.info(
                "Role assignment already exists for accountId={} roleCode={}",
                accountId,
                normalizedRoleCode,
            )
        } else {
            logger.info(
                "Assigned roleCode={} to accountId={}",
                normalizedRoleCode,
                accountId,
            )
        }

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

        val roles =
            assignments
                .map { assignment ->
                    rolesById.getValue(assignment.roleId).toData().toAccountRoleResponse()
                }.sortedBy(AccountRoleResponse::code)

        logger.info(
            "Loaded {} role assignments for accountId={}",
            roles.size,
            accountId,
        )

        return roles
    }

    private fun requireAccount(accountId: UUID) {
        if (!accountRepository.existsById(accountId)) {
            logger.warn(
                "Rejected account-role operation because accountId={} was not found",
                accountId,
            )
            throw AccountNotFoundException(accountId)
        }
    }
}
