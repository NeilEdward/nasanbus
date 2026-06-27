package com.nasanbus.users.repository

import com.nasanbus.users.entity.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AccountRepository : JpaRepository<AccountEntity, UUID> {
    fun findByCognitoSub(cognitoSub: String): AccountEntity?
}
