package com.nasanbus.common.config

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.assertNotEquals

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @ParameterizedTest
    @ValueSource(
        strings = [
            "/api/v1/accounts",
            "/api/v1/roles",
            "/api/v1/accounts/00000000-0000-0000-0000-000000000000/roles",
        ],
    )
    fun `api endpoints require authentication`(path: String) {
        mockMvc.perform(get(path))
            .andExpect(status().isUnauthorized)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "/api/v1/accounts",
            "/api/v1/roles",
            "/api/v1/accounts/00000000-0000-0000-0000-000000000000/roles",
        ],
    )
    fun `api endpoints accept jwt authentication`(path: String) {
        mockMvc.perform(get(path).with(jwt()))
            .andExpect { result -> assertNotEquals(401, result.response.status) }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "/actuator/health",
            "/actuator/info",
            "/swagger-ui.html",
            "/v3/api-docs",
        ],
    )
    fun `public endpoints do not require authentication`(path: String) {
        mockMvc.perform(get(path))
            .andExpect { result -> assertNotEquals(401, result.response.status) }
    }
}
