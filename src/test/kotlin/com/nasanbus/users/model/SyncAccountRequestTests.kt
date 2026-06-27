package com.nasanbus.users.model

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SyncAccountRequestTests {
    @Test
    fun `request only accepts profile fallback fields`() {
        val requestFields = SyncAccountRequest::class.java.declaredFields
            .map { it.name }
            .toSet()

        assertTrue(requestFields.containsAll(setOf("firstName", "lastName", "phoneNumber")))
        assertFalse(requestFields.contains("cognitoSub"))
        assertFalse(requestFields.contains("email"))
        assertFalse(requestFields.contains("role"))
        assertFalse(requestFields.contains("roles"))
        assertFalse(requestFields.contains("status"))
    }
}
