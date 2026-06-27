package com.nasanbus.users.controller

import com.nasanbus.auth.CognitoUserClaims
import com.nasanbus.common.exception.ConflictException
import com.nasanbus.users.model.AccountStatus
import com.nasanbus.users.model.SyncAccountResponse
import com.nasanbus.users.service.AccountService
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
class AccountSyncControllerTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var accountService: AccountService

    @Test
    fun `sync endpoint requires authentication`() {
        mockMvc
            .perform(post("/api/v1/accounts/sync"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `sync endpoint uses authenticated jwt claims`() {
        val accountId = UUID.randomUUID()
        val claims =
            CognitoUserClaims(
                subject = "cognito-user-sub",
                email = "admin@nasanbus.test",
                firstName = "Admin",
                lastName = "User",
                phoneNumber = "+639171234567",
            )

        Mockito
            .`when`(accountService.syncAccountFromCognito(claims))
            .thenReturn(
                SyncAccountResponse(
                    id = accountId,
                    cognitoSub = "cognito-user-sub",
                    email = "admin@nasanbus.test",
                    firstName = "Admin",
                    lastName = "User",
                    phoneNumber = "+639171234567",
                    status = AccountStatus.ACTIVE,
                    roles = listOf("ADMIN"),
                ),
            )

        mockMvc
            .perform(
                post("/api/v1/accounts/sync")
                    .with(
                        jwt()
                            .jwt {
                                it.subject("cognito-user-sub")
                                it.claim("email", "admin@nasanbus.test")
                                it.claim("given_name", "Admin")
                                it.claim("family_name", "User")
                                it.claim("phone_number", "+639171234567")
                            },
                    ),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id", equalTo(accountId.toString())))
            .andExpect(jsonPath("$.cognitoSub", equalTo("cognito-user-sub")))
            .andExpect(jsonPath("$.email", equalTo("admin@nasanbus.test")))
            .andExpect(jsonPath("$.firstName", equalTo("Admin")))
            .andExpect(jsonPath("$.lastName", equalTo("User")))
            .andExpect(jsonPath("$.phoneNumber", equalTo("+639171234567")))
            .andExpect(jsonPath("$.status", equalTo("ACTIVE")))
            .andExpect(jsonPath("$.roles[0]", equalTo("ADMIN")))

        Mockito.verify(accountService).syncAccountFromCognito(claims)
    }

    @Test
    fun `sync endpoint returns conflict when email belongs to another Cognito subject`() {
        val claims =
            CognitoUserClaims(
                subject = "new-cognito-sub",
                email = "admin@nasanbus.test",
                firstName = "Admin",
                lastName = "User",
                phoneNumber = null,
            )

        Mockito
            .`when`(accountService.syncAccountFromCognito(claims))
            .thenThrow(ConflictException("Account email is already linked to another Cognito user"))

        mockMvc
            .perform(
                post("/api/v1/accounts/sync")
                    .with(
                        jwt()
                            .jwt {
                                it.subject("new-cognito-sub")
                                it.claim("email", "admin@nasanbus.test")
                                it.claim("given_name", "Admin")
                                it.claim("family_name", "User")
                            },
                    ),
            ).andExpect(status().isConflict)
            .andExpect(jsonPath("$.message", equalTo("Account email is already linked to another Cognito user")))
    }
}
