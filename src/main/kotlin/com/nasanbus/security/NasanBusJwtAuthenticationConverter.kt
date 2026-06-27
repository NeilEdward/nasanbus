package com.nasanbus.security

import com.nasanbus.users.model.AccountStatus
import com.nasanbus.users.repository.AccountRepository
import com.nasanbus.users.repository.AccountRoleRepository
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component

@Component
class NasanBusJwtAuthenticationConverter(
    private val accountRepository: AccountRepository,
    private val accountRoleRepository: AccountRoleRepository,
) : Converter<Jwt, AbstractAuthenticationToken> {
    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val authorities = resolveAuthorities(jwt)

        return JwtAuthenticationToken(
            jwt,
            authorities,
            jwt.subject,
        )
    }

    private fun resolveAuthorities(jwt: Jwt): Collection<SimpleGrantedAuthority> {
        val account =
            accountRepository.findByCognitoSub(jwt.subject)
                ?: return emptyList()

        if (account.status != AccountStatus.ACTIVE) {
            return emptyList()
        }

        val accountId = account.id ?: return emptyList()

        return accountRoleRepository
            .findRoleCodesByAccountId(accountId)
            .map { roleCode -> SimpleGrantedAuthority(roleCode) }
    }
}
