package com.lucas.instagram.clone.entrypoint.controller

import com.lucas.instagram.clone.core.mappers.UserConverter
import com.lucas.instagram.clone.core.ports.UserServicePort
import com.lucas.instagram.clone.entrypoint.dto.UserRegisterRequest
import com.lucas.instagram.clone.infrastructure.entity.UserEntity
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@Controller("/users")
class UserController(
    private val userService: UserServicePort,
) {

    @Post("/add")
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun addUser(@Body request: UserRegisterRequest): HttpResponse<*>?{
        val userFind = userService.findByUsername(request.username)
        if(userFind != null){
            return HttpResponse.ok(userFind.likePosts)
//            throw RuntimeException("User already exists")
        }
        val user = UserConverter.userRegisterRequestToUserEntity(request)
        userService.saveUser(user)

        return HttpResponse.ok(userFind)
    }

    @Post("/find/email")
    @Consumes(MediaType.TEXT_PLAIN)
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun findByEmail(@Body email: String): UserEntity? {
        val userEntity = userService.findByEmail(email)
        return userEntity
    }

}