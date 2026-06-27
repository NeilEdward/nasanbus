package com.nasanbus.auth

import org.springframework.security.oauth2.jwt.Jwt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CognitoClaimExtractorTests {
    private val extractor = CognitoClaimExtractor()

    @Test
    fun `extracts supported Cognito claims from jwt`() {
        val claims =
            extractor.extract(
                jwt {
                    subject("cognito-user-sub")
                    claim("email", "admin@nasanbus.test")
                    claim("given_name", "Admin")
                    claim("family_name", "User")
                    claim("phone_number", "+639171234567")
                },
            )

        assertEquals("cognito-user-sub", claims.subject)
        assertEquals("admin@nasanbus.test", claims.email)
        assertEquals("Admin", claims.firstName)
        assertEquals("User", claims.lastName)
        assertEquals("+639171234567", claims.phoneNumber)
    }

    @Test
    fun `requires email claim`() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                extractor.extract(
                    jwt {
                        subject("cognito-user-sub")
                    },
                )
            }

        assertEquals("Email claim is missing from JWT", exception.message)
    }

    @Test
    fun `requires subject claim`() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                extractor.extract(
                    jwt {
                        claim("email", "admin@nasanbus.test")
                    },
                )
            }

        assertEquals("Subject claim is missing from JWT", exception.message)
    }

    private fun jwt(configure: Jwt.Builder.() -> Unit): Jwt =
        Jwt
            .withTokenValue("token")
            .header("alg", "none")
            .apply(configure)
            .build()
}
