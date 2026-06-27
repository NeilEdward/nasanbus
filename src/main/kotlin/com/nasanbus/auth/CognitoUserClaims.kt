package com.nasanbus.auth

data class CognitoUserClaims(
    val subject: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
)
