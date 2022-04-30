package com.lucas.instagram.clone.core.ports

import com.lucas.instagram.clone.core.model.TokenVerified
import com.lucas.instagram.clone.core.model.UserLoginDomain
import com.lucas.instagram.clone.core.model.UserRegisterDomain

interface OpenIdServicePort {
    fun login(userLogin: UserLoginDomain): String
    fun verifyToken(token: String): TokenVerified
    fun registerUser(user: UserRegisterDomain): String?
}