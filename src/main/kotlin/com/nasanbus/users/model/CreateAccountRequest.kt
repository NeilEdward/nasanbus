package com.nasanbus.users.model

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateAccountRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val cognitoSub: String,
    @field:NotBlank
    @field:Email
    @field:Size(max = 255)
    val email: String,
    @field:Size(max = 100)
    val firstName: String? = null,
    @field:Size(max = 100)
    val lastName: String? = null,
    @field:Size(max = 30)
    val phoneNumber: String? = null,
)

fun CreateAccountRequest.toData() =
    Account(
        cognitoSub = cognitoSub,
        email = email,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        addedBy = cognitoSub,
        updatedBy = cognitoSub,
    )
