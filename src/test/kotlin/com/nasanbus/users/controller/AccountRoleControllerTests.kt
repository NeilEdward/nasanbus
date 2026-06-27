package com.nasanbus.users.controller

import com.nasanbus.users.model.AccountRoleResponse
import com.nasanbus.users.service.AccountRoleService
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
class AccountRoleControllerTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var accountRoleService: AccountRoleService

    @Test
    fun `assign role endpoint requires authentication`() {
        mockMvc
            .perform(
                post("/api/v1/accounts/${UUID.randomUUID()}/roles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"roleCode":"DRIVER"}"""),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `assign role endpoint rejects authenticated user without admin authority`() {
        mockMvc
            .perform(
                post("/api/v1/accounts/${UUID.randomUUID()}/roles")
                    .with(jwt())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"roleCode":"DRIVER"}"""),
            ).andExpect(status().isForbidden)
    }

    @Test
    fun `assign role endpoint allows admin authority`() {
        val accountId = UUID.randomUUID()

        Mockito
            .`when`(accountRoleService.assignRoleToAccount(accountId, "DRIVER"))
            .thenReturn(AccountRoleResponse("DRIVER", "Driver"))

        mockMvc
            .perform(
                post("/api/v1/accounts/$accountId/roles")
                    .with(jwt().authorities(SimpleGrantedAuthority("ADMIN")))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"roleCode":"DRIVER"}"""),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.code", equalTo("DRIVER")))
            .andExpect(jsonPath("$.name", equalTo("Driver")))
    }

    @Test
    fun `get account roles endpoint rejects authenticated user without admin authority`() {
        mockMvc
            .perform(
                get("/api/v1/accounts/${UUID.randomUUID()}/roles")
                    .with(jwt()),
            ).andExpect(status().isForbidden)
    }

    @Test
    fun `get account roles endpoint allows admin authority`() {
        val accountId = UUID.randomUUID()

        Mockito
            .`when`(accountRoleService.getAccountRoles(accountId))
            .thenReturn(listOf(AccountRoleResponse("ADMIN", "Administrator")))

        mockMvc
            .perform(
                get("/api/v1/accounts/$accountId/roles")
                    .with(jwt().authorities(SimpleGrantedAuthority("ADMIN"))),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].code", equalTo("ADMIN")))
            .andExpect(jsonPath("$[0].name", equalTo("Administrator")))
    }
}
