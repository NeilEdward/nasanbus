package com.nasanbus.security

import com.nasanbus.users.entity.AccountEntity
import com.nasanbus.users.model.AccountStatus
import com.nasanbus.users.repository.AccountRepository
import com.nasanbus.users.repository.AccountRoleRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.oauth2.jwt.Jwt
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NasanBusJwtAuthenticationConverterTests {
    @Test
    fun `active synced account receives PostgreSQL role authorities`() {
        val accountId = UUID.randomUUID()
        val accountRepository = Mockito.mock(AccountRepository::class.java)
        val accountRoleRepository = Mockito.mock(AccountRoleRepository::class.java)
        val converter = NasanBusJwtAuthenticationConverter(accountRepository, accountRoleRepository)

        Mockito
            .`when`(accountRepository.findByCognitoSub("cognito-user-sub"))
            .thenReturn(account(accountId, AccountStatus.ACTIVE))
        Mockito
            .`when`(accountRoleRepository.findRoleCodesByAccountId(accountId))
            .thenReturn(listOf("ADMIN", "DRIVER"))

        val authentication = converter.convert(jwt("cognito-user-sub"))

        assertEquals("cognito-user-sub", authentication.name)
        assertEquals(listOf("ADMIN", "DRIVER"), authentication.authorities.map { it.authority })
    }

    @Test
    fun `unsynced account receives no authorities`() {
        val accountRepository = Mockito.mock(AccountRepository::class.java)
        val accountRoleRepository = Mockito.mock(AccountRoleRepository::class.java)
        val converter = NasanBusJwtAuthenticationConverter(accountRepository, accountRoleRepository)

        Mockito
            .`when`(accountRepository.findByCognitoSub("unsynced-cognito-sub"))
            .thenReturn(null)

        val authentication = converter.convert(jwt("unsynced-cognito-sub"))

        assertTrue(authentication.authorities.isEmpty())
        Mockito.verifyNoInteractions(accountRoleRepository)
    }

    @Test
    fun `inactive account receives no authorities`() {
        val accountId = UUID.randomUUID()
        val accountRepository = Mockito.mock(AccountRepository::class.java)
        val accountRoleRepository = Mockito.mock(AccountRoleRepository::class.java)
        val converter = NasanBusJwtAuthenticationConverter(accountRepository, accountRoleRepository)

        Mockito
            .`when`(accountRepository.findByCognitoSub("cognito-user-sub"))
            .thenReturn(account(accountId, AccountStatus.INACTIVE))

        val authentication = converter.convert(jwt("cognito-user-sub"))

        assertTrue(authentication.authorities.isEmpty())
        Mockito.verifyNoInteractions(accountRoleRepository)
    }

    @Test
    fun `suspended account receives no authorities`() {
        val accountId = UUID.randomUUID()
        val accountRepository = Mockito.mock(AccountRepository::class.java)
        val accountRoleRepository = Mockito.mock(AccountRoleRepository::class.java)
        val converter = NasanBusJwtAuthenticationConverter(accountRepository, accountRoleRepository)

        Mockito
            .`when`(accountRepository.findByCognitoSub("cognito-user-sub"))
            .thenReturn(account(accountId, AccountStatus.SUSPENDED))

        val authentication = converter.convert(jwt("cognito-user-sub"))

        assertTrue(authentication.authorities.isEmpty())
        Mockito.verifyNoInteractions(accountRoleRepository)
    }

    private fun jwt(subject: String): Jwt =
        Jwt
            .withTokenValue("token")
            .header("alg", "none")
            .subject(subject)
            .build()

    private fun account(
        accountId: UUID,
        status: AccountStatus,
    ): AccountEntity =
        AccountEntity(
            id = accountId,
            cognitoSub = "cognito-user-sub",
            email = "admin@nasanbus.test",
            status = status,
            addedOn = LocalDateTime.now(),
            addedBy = "cognito-user-sub",
            updatedBy = "cognito-user-sub",
            updatedOn = LocalDateTime.now(),
        )
}
