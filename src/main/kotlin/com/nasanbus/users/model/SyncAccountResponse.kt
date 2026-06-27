package com.nasanbus.users.model

import java.util.UUID

data class SyncAccountResponse(
    val id: UUID,
    val cognitoSub: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val status: String,
    val roles: List<String>,
)
