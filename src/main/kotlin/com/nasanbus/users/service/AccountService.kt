package com.nasanbus.users.service

import com.nasanbus.users.entity.AccountEntity
import com.nasanbus.users.model.Account
import com.nasanbus.users.repository.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class AccountService(
    private val accountRepository: AccountRepository,
) {
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
}

private fun Account.toEntity() =
    AccountEntity(
        id = id,
        cognitoSub = cognitoSub,
        email = email,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        addedOn = addedOn,
        addedBy = addedBy,
        updatedBy = updatedBy,
        updatedOn = updatedOn,
        deletedBy = deletedBy,
        deletedOn = deletedOn,
    )

private fun AccountEntity.toData() =
    Account(
        id = id,
        cognitoSub = cognitoSub,
        email = email,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        addedOn = addedOn,
        addedBy = addedBy,
        updatedBy = updatedBy,
        updatedOn = updatedOn,
        deletedBy = deletedBy,
        deletedOn = deletedOn,
    )
