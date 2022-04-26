package com.lucas.instagram.clone.core.mappers

import com.lucas.instagram.clone.core.model.UserLoginDomain
import com.lucas.instagram.clone.entrypoint.dto.LoginRequest

class LoginConverter {
    companion object{
        fun loguinRequestToUserLoginDomain(loginRequest: LoginRequest): UserLoginDomain = UserLoginDomain(
            loginRequest.username,
            loginRequest.password
        )
    }
}