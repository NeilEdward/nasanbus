package com.nasanbus.users.service

import com.nasanbus.users.entity.AccountEntity
import com.nasanbus.users.model.AccountRoleResponse
import com.nasanbus.users.repository.AccountRepository
import com.nasanbus.users.repository.AccountRoleRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@SpringBootTest
@Transactional
class AccountRoleServiceTests {
    @Autowired
    private lateinit var accountRoleService: AccountRoleService

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var accountRoleRepository: AccountRoleRepository

    @Test
    fun `assigning the same role twice remains idempotent`() {
        val accountId = createAccount()

        val firstAssignment = accountRoleService.assignRoleToAccount(accountId, "ADMIN")
        val secondAssignment = accountRoleService.assignRoleToAccount(accountId, "admin")

        assertEquals(AccountRoleResponse("ADMIN", "Administrator"), firstAssignment)
        assertEquals(firstAssignment, secondAssignment)
        assertEquals(1, accountRoleRepository.findByAccountId(accountId).size)
        assertEquals(listOf(firstAssignment), accountRoleService.getAccountRoles(accountId))
    }

    @Test
    fun `assigning a role requires an existing account`() {
        assertFailsWith<AccountNotFoundException> {
            accountRoleService.assignRoleToAccount(UUID.randomUUID(), "ADMIN")
        }
    }

    @Test
    fun `assigning a role requires an existing role code`() {
        val accountId = createAccount()

        assertFailsWith<RoleNotFoundException> {
            accountRoleService.assignRoleToAccount(accountId, "SUPERADMIN")
        }
    }

    private fun createAccount(): UUID {
        val accountId = UUID.randomUUID()
        val now = LocalDateTime.now()

        accountRepository.saveAndFlush(
            AccountEntity(
                id = accountId,
                cognitoSub = "test-$accountId",
                email = "$accountId@nasanbus.test",
                addedOn = now,
                addedBy = "TEST",
                updatedBy = "TEST",
                updatedOn = now,
            ),
        )

        return accountId
    }
}
