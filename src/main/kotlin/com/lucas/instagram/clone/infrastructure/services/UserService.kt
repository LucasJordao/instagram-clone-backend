package com.lucas.instagram.clone.infrastructure.services

import com.lucas.instagram.clone.common.utils.Encrypt
import com.lucas.instagram.clone.core.mappers.UserConverter
import com.lucas.instagram.clone.core.ports.OpenIdServicePort
import com.lucas.instagram.clone.core.ports.UserServicePort
import com.lucas.instagram.clone.infrastructure.entity.Like
import com.lucas.instagram.clone.infrastructure.entity.UserEntity
import com.lucas.instagram.clone.infrastructure.repository.UserRepository
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import java.util.*

@Singleton
class UserService(
    private val userRepository: UserRepository,
    private val openIdService: OpenIdServicePort
): UserServicePort {

    @Value("\${encrypt.secretKey}")
    var secretKey: String? = null

    override fun saveUser(user: UserEntity) {
        val likePost = Like(
            userLike = user
        )

        try{
            val userId = openIdService.registerUser(UserConverter.userEntityToUserRegisterDomain(user))
            user.id = UUID.fromString(userId)
            user.password = Encrypt.encrypt(user.password, secretKey!!)

            user.likePosts = likePost
            userRepository.save(user)
        }catch (e: RuntimeException){
            println("Usuario já cadastrado no keycloak")
        }

    }

    override fun findByEmail(email: String): UserEntity? {
        val userEntity = userRepository.findByEmail(email.toLowerCase())

        if(userEntity.isPresent){
            return userEntity.get()
        }

        return null
    }

    override fun findByUsername(username: String): UserEntity? {
        val userEntity = userRepository.findByUsername(username.toLowerCase())

        if(userEntity.isPresent){
            return userEntity.get()
        }

        return null
    }
}