package com.lucas.instagram.clone.core.ports

import com.lucas.instagram.clone.core.model.TokenVerified
import com.lucas.instagram.clone.core.model.UserLoginDomain

interface OpenIdServicePort {
    fun login(userLogin: UserLoginDomain): String
    fun verifyToken(token: String): TokenVerified
}