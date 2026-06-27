package com.nasanbus.users.model

import com.nasanbus.auth.CognitoUserClaims
import com.nasanbus.common.model.Auditable
import com.nasanbus.users.entity.AccountEntity
import java.time.LocalDateTime
import java.util.UUID

data class Account(
    val id: UUID? = null,
    val cognitoSub: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val status: AccountStatus = AccountStatus.ACTIVE,
    override val addedOn: LocalDateTime? = null,
    override val addedBy: String,
    override val updatedBy: String,
    override val updatedOn: LocalDateTime? = null,
    override val deletedBy: String? = null,
    override val deletedOn: LocalDateTime? = null,
) : Auditable

internal fun CognitoUserClaims.toAccountEntity() =
    LocalDateTime.now().let { now ->
        AccountEntity(
            id = UUID.randomUUID(),
            cognitoSub = subject,
            email = email,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            addedOn = now,
            addedBy = subject,
            updatedBy = subject,
            updatedOn = now,
        )
    }

internal fun Account.toEntity() =
    AccountEntity(
        id = id,
        cognitoSub = cognitoSub,
        email = email,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        status = status,
        addedOn = addedOn,
        addedBy = addedBy,
        updatedBy = updatedBy,
        updatedOn = updatedOn,
        deletedBy = deletedBy,
        deletedOn = deletedOn,
    )

internal fun AccountEntity.toData() =
    Account(
        id = id,
        cognitoSub = cognitoSub,
        email = email,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        status = status,
        addedOn = addedOn,
        addedBy = addedBy,
        updatedBy = updatedBy,
        updatedOn = updatedOn,
        deletedBy = deletedBy,
        deletedOn = deletedOn,
    )

internal fun AccountEntity.toSyncResponse(roles: List<String>) =
    SyncAccountResponse(
        id = requireNotNull(id),
        cognitoSub = cognitoSub,
        email = email,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        status = status,
        roles = roles,
    )

enum class AccountStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
}
