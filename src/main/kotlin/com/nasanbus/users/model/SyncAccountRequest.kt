package com.nasanbus.users.model

data class SyncAccountRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
)
