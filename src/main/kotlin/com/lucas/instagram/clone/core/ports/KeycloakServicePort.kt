package com.lucas.instagram.clone.core.ports

import com.lucas.instagram.clone.core.model.UserLoginDomain

interface KeycloakServicePort {
    fun login(userLogin: UserLoginDomain): String
}