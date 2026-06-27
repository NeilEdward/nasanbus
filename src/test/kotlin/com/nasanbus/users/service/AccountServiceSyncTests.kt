package com.nasanbus.users.service

import com.nasanbus.auth.CognitoUserClaims
import com.nasanbus.common.exception.ConflictException
import com.nasanbus.users.entity.AccountEntity
import com.nasanbus.users.repository.AccountRepository
import com.nasanbus.users.repository.AccountRoleRepository
import org.junit.jupiter.api.Test
import java.lang.reflect.Proxy
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class AccountServiceSyncTests {
    @Test
    fun `sync returns existing account by Cognito subject`() {
        val accountId = UUID.randomUUID()
        val account =
            AccountEntity(
                id = accountId,
                cognitoSub = "cognito-user-sub",
                email = "admin@nasanbus.test",
                firstName = "Admin",
                lastName = "User",
                phoneNumber = "+639171234567",
                status = "ACTIVE",
                addedOn = LocalDateTime.now(),
                addedBy = "cognito-user-sub",
                updatedBy = "cognito-user-sub",
                updatedOn = LocalDateTime.now(),
            )
        var saveCalled = false
        val accountService =
            AccountService(
                accountRepository(
                    accountByCognitoSub = account,
                    onSave = {
                        saveCalled = true
                        it
                    },
                ),
                accountRoleRepository(listOf("ADMIN", "DRIVER")),
            )

        val response =
            accountService.syncAccountFromCognito(
                CognitoUserClaims(
                    subject = "cognito-user-sub",
                    email = "changed@nasanbus.test",
                    firstName = "Changed",
                    lastName = "Name",
                    phoneNumber = null,
                ),
            )

        assertEquals(accountId, response.id)
        assertEquals("cognito-user-sub", response.cognitoSub)
        assertEquals("admin@nasanbus.test", response.email)
        assertEquals("Admin", response.firstName)
        assertEquals("User", response.lastName)
        assertEquals("+639171234567", response.phoneNumber)
        assertEquals("ACTIVE", response.status)
        assertEquals(listOf("ADMIN", "DRIVER"), response.roles)
        assertFalse(saveCalled)
    }

    @Test
    fun `sync creates account when Cognito subject does not exist`() {
        var savedAccount: AccountEntity? = null
        val accountService =
            AccountService(
                accountRepository(
                    accountByCognitoSub = null,
                    onSave = {
                        savedAccount = it
                        it
                    },
                ),
                accountRoleRepository(emptyList()),
            )

        val response =
            accountService.syncAccountFromCognito(
                CognitoUserClaims(
                    subject = "cognito-user-sub",
                    email = "admin@nasanbus.test",
                    firstName = "Admin",
                    lastName = "User",
                    phoneNumber = "+639171234567",
                ),
            )

        val createdAccount = assertNotNull(savedAccount)
        assertNotNull(createdAccount.id)
        assertEquals("cognito-user-sub", createdAccount.cognitoSub)
        assertEquals("admin@nasanbus.test", createdAccount.email)
        assertEquals("Admin", createdAccount.firstName)
        assertEquals("User", createdAccount.lastName)
        assertEquals("+639171234567", createdAccount.phoneNumber)
        assertEquals("ACTIVE", createdAccount.status)
        assertEquals("cognito-user-sub", createdAccount.addedBy)
        assertEquals("cognito-user-sub", createdAccount.updatedBy)

        assertEquals(createdAccount.id, response.id)
        assertEquals(createdAccount.cognitoSub, response.cognitoSub)
        assertEquals(createdAccount.email, response.email)
        assertEquals(createdAccount.firstName, response.firstName)
        assertEquals(createdAccount.lastName, response.lastName)
        assertEquals(createdAccount.phoneNumber, response.phoneNumber)
        assertEquals(createdAccount.status, response.status)
        assertEquals(emptyList(), response.roles)
    }

    @Test
    fun `sync rejects duplicate email under different Cognito subject`() {
        var saveCalled = false
        val existingAccount =
            AccountEntity(
                id = UUID.randomUUID(),
                cognitoSub = "existing-cognito-sub",
                email = "admin@nasanbus.test",
                addedOn = LocalDateTime.now(),
                addedBy = "existing-cognito-sub",
                updatedBy = "existing-cognito-sub",
                updatedOn = LocalDateTime.now(),
            )
        val accountService =
            AccountService(
                accountRepository(
                    accountByCognitoSub = null,
                    accountByEmail = existingAccount,
                    onSave = {
                        saveCalled = true
                        it
                    },
                ),
                accountRoleRepository(emptyList()),
            )

        val exception =
            assertFailsWith<ConflictException> {
                accountService.syncAccountFromCognito(
                    CognitoUserClaims(
                        subject = "new-cognito-sub",
                        email = "admin@nasanbus.test",
                        firstName = "Admin",
                        lastName = "User",
                        phoneNumber = null,
                    ),
                )
            }

        assertEquals(
            "Account email is already linked to another Cognito user",
            exception.message,
        )
        assertFalse(saveCalled)
    }

    private fun accountRepository(
        accountByCognitoSub: AccountEntity?,
        accountByEmail: AccountEntity? = null,
        onSave: (AccountEntity) -> AccountEntity,
    ): AccountRepository =
        proxy { methodName, arguments ->
            when (methodName) {
                "findByCognitoSub" -> accountByCognitoSub
                "findByEmail" -> accountByEmail
                "save" -> onSave(arguments[0] as AccountEntity)
                else -> unexpectedMethod(methodName)
            }
        }

    private fun accountRoleRepository(roles: List<String>): AccountRoleRepository =
        proxy { methodName, _ ->
            when (methodName) {
                "findRoleCodesByAccountId" -> roles
                else -> unexpectedMethod(methodName)
            }
        }

    private inline fun <reified T> proxy(
        crossinline handler: (String, Array<Any?>) -> Any?,
    ): T =
        Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java),
        ) { _, method, arguments ->
            handler(method.name, arguments ?: emptyArray())
        } as T

    private fun unexpectedMethod(methodName: String): Nothing {
        throw UnsupportedOperationException("Unexpected repository method: $methodName")
    }
}
