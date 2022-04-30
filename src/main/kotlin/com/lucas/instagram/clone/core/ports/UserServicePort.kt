package com.lucas.instagram.clone.core.ports

import com.lucas.instagram.clone.infrastructure.entity.UserEntity

interface UserServicePort {
    fun saveUser(user: UserEntity)
    fun findByEmail(email: String): UserEntity?
    fun findByUsername(username: String): UserEntity?
}