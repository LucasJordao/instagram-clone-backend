package com.lucas.instagram.clone.entrypoint.controller

import com.lucas.instagram.clone.core.mappers.LoginConverter
import com.lucas.instagram.clone.core.mappers.UserConverter
import com.lucas.instagram.clone.core.ports.OpenIdServicePort
import com.lucas.instagram.clone.core.ports.UserServicePort
import com.lucas.instagram.clone.entrypoint.dto.LoginRequest
import com.lucas.instagram.clone.entrypoint.dto.UserRegisterRequest
import com.lucas.instagram.clone.infrastructure.entity.UserEntity
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule

@Controller("/instagram")
class LoginController(
    private val keycloakService: OpenIdServicePort
) {

    @Post("/login")
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun login(@Body request: LoginRequest): MutableHttpResponse<String>? {
        val token: String = keycloakService.login(
            LoginConverter.loguinRequestToUserLoginDomain(request)
        )
        return HttpResponse.ok(token)
    }


    @Post("/token/verify")
    @Consumes(MediaType.TEXT_PLAIN)
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun verifyToken(@Body token: String): HttpResponse<*>{
        return HttpResponse.ok(keycloakService.verifyToken(token))
    }

    @Get("/admin")
    @Secured("admin")
    fun admin(authentication: Authentication): MutableHttpResponse<String>? {
        return HttpResponse.ok("Olá mundo ${authentication.roles}")
    }
}