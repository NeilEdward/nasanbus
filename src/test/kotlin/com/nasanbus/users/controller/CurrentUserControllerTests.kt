package com.nasanbus.users.controller

import com.nasanbus.common.exception.ForbiddenException
import com.nasanbus.common.exception.NotFoundException
import com.nasanbus.users.model.AccountStatus
import com.nasanbus.users.model.CurrentUserResponse
import com.nasanbus.users.service.AccountService
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
class CurrentUserControllerTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var accountService: AccountService

    @Test
    fun `current user endpoint requires authentication`() {
        mockMvc
            .perform(get("/api/v1/me"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `current user endpoint uses authenticated jwt subject`() {
        val accountId = UUID.randomUUID()

        Mockito
            .`when`(accountService.getCurrentUser("cognito-user-sub"))
            .thenReturn(
                CurrentUserResponse(
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
                get("/api/v1/me")
                    .with(
                        jwt()
                            .jwt {
                                it.subject("cognito-user-sub")
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

        Mockito.verify(accountService).getCurrentUser("cognito-user-sub")
    }

    @Test
    fun `current user endpoint returns empty roles`() {
        val accountId = UUID.randomUUID()

        Mockito
            .`when`(accountService.getCurrentUser("cognito-user-sub"))
            .thenReturn(
                CurrentUserResponse(
                    id = accountId,
                    cognitoSub = "cognito-user-sub",
                    email = "commuter@nasanbus.test",
                    firstName = "Commuter",
                    lastName = "User",
                    phoneNumber = null,
                    status = AccountStatus.ACTIVE,
                    roles = emptyList(),
                ),
            )

        mockMvc
            .perform(
                get("/api/v1/me")
                    .with(jwt().jwt { it.subject("cognito-user-sub") }),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.roles", hasSize<Any>(0)))
    }

    @Test
    fun `current user endpoint returns not found for unsynced user`() {
        Mockito
            .`when`(accountService.getCurrentUser("unsynced-cognito-sub"))
            .thenThrow(NotFoundException("Account not found"))

        mockMvc
            .perform(
                get("/api/v1/me")
                    .with(jwt().jwt { it.subject("unsynced-cognito-sub") }),
            ).andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message", equalTo("Account not found")))
    }

    @Test
    fun `current user endpoint returns forbidden for inactive account`() {
        Mockito
            .`when`(accountService.getCurrentUser("cognito-user-sub"))
            .thenThrow(ForbiddenException("Account is inactive"))

        mockMvc
            .perform(
                get("/api/v1/me")
                    .with(jwt().jwt { it.subject("cognito-user-sub") }),
            ).andExpect(status().isForbidden)
            .andExpect(jsonPath("$.message", equalTo("Account is inactive")))
    }

    @Test
    fun `current user endpoint returns forbidden for suspended account`() {
        Mockito
            .`when`(accountService.getCurrentUser("cognito-user-sub"))
            .thenThrow(ForbiddenException("Account is suspended"))

        mockMvc
            .perform(
                get("/api/v1/me")
                    .with(jwt().jwt { it.subject("cognito-user-sub") }),
            ).andExpect(status().isForbidden)
            .andExpect(jsonPath("$.message", equalTo("Account is suspended")))
    }
}
