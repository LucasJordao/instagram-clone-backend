package com.lucas.instagram.clone.core.mappers

import com.lucas.instagram.clone.core.model.UserRegisterDomain
import com.lucas.instagram.clone.entrypoint.dto.UserRegisterRequest
import com.lucas.instagram.clone.infrastructure.entity.UserEntity

class UserConverter {
    companion object{
        fun userRegisterRequestToUserEntity(request: UserRegisterRequest): UserEntity = UserEntity(
            username = request.username,
            password = request.password,
            perfilImage = request.perfilImage,
            email = request.email,
            name = request.name
        )

        fun userEntityToUserRegisterDomain(entity: UserEntity): UserRegisterDomain = UserRegisterDomain(
            username = entity.username,
            password = entity.password,
        )
    }
}