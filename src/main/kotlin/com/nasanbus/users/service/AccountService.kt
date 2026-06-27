package com.nasanbus.users.service

import com.nasanbus.auth.CognitoUserClaims
import com.nasanbus.common.exception.ConflictException
import com.nasanbus.users.entity.AccountEntity
import com.nasanbus.users.model.Account
import com.nasanbus.users.model.SyncAccountResponse
import com.nasanbus.users.model.toAccountEntity
import com.nasanbus.users.model.toData
import com.nasanbus.users.model.toEntity
import com.nasanbus.users.model.toSyncResponse
import com.nasanbus.users.repository.AccountRoleRepository
import com.nasanbus.users.repository.AccountRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val accountRoleRepository: AccountRoleRepository,
) {
    private companion object {
        private val logger = LoggerFactory.getLogger(AccountService::class.java)
    }

    @Transactional
    fun create(data: Account): Account {
        val now = LocalDateTime.now()
        val account = data.copy(id = UUID.randomUUID(), addedOn = now, updatedOn = now)

        return accountRepository.save(account.toEntity()).toData()
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Account> = accountRepository.findAll().map(AccountEntity::toData)

    @Transactional(readOnly = true)
    fun findById(id: UUID): Account? = accountRepository.findById(id).orElse(null)?.toData()

    @Transactional
    fun syncAccountFromCognito(claims: CognitoUserClaims): SyncAccountResponse {
        val account =
            accountRepository.findByCognitoSub(claims.subject)?.also {
                logger.info(
                    "Synced existing account id={} cognitoSub={}",
                    it.id,
                    claims.subject,
                )
            } ?: createSyncedAccount(claims)

        val accountId = requireNotNull(account.id)
        val roles = accountRoleRepository.findRoleCodesByAccountId(accountId)

        return account.toSyncResponse(roles)
    }

    private fun createSyncedAccount(claims: CognitoUserClaims): AccountEntity {
        accountRepository.findByEmail(claims.email)?.let { existingAccount ->
            logger.warn(
                "Rejected account sync for cognitoSub={} because email={} is already linked to account id={} cognitoSub={}",
                claims.subject,
                claims.email,
                existingAccount.id,
                existingAccount.cognitoSub,
            )
            throw ConflictException("Account email is already linked to another Cognito user")
        }

        return accountRepository.save(claims.toAccountEntity()).also {
            logger.info(
                "Created synced account id={} cognitoSub={}",
                it.id,
                claims.subject,
            )
        }
    }
}
