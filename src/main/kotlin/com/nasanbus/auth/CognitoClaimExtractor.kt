package com.nasanbus.auth

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class CognitoClaimExtractor {
    fun extract(jwt: Jwt): CognitoUserClaims =
        CognitoUserClaims(
            subject = requireNotNull(jwt.subject) {
                "Subject claim is missing from JWT"
            },
            email = requireNotNull(jwt.claimAsString("email")) {
                "Email claim is missing from JWT"
            },
            firstName = jwt.claimAsString("given_name"),
            lastName = jwt.claimAsString("family_name"),
            phoneNumber = jwt.claimAsString("phone_number"),
        )

    private fun Jwt.claimAsString(name: String): String? =
        claims[name] as? String
}
