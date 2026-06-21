package com.nasanbus.users.service

import com.nasanbus.users.entity.RoleEntity
import com.nasanbus.users.entity.toData
import com.nasanbus.users.model.Role
import com.nasanbus.users.repository.RoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleService(
    private val roleRepository: RoleRepository,
) {
    @Transactional(readOnly = true)
    fun getRoles(): List<Role> = roleRepository.findAll().map(RoleEntity::toData)

    @Transactional(readOnly = true)
    fun getRoleByCode(code: String): Role? = roleRepository.findByCode(code)?.toData()
}
