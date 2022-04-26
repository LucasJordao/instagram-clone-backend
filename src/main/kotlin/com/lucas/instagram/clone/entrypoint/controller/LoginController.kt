package com.lucas.instagram.clone.entrypoint.controller

import com.lucas.instagram.clone.core.mappers.LoginConverter
import com.lucas.instagram.clone.core.ports.KeycloakServicePort
import com.lucas.instagram.clone.entrypoint.dto.LoginRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule

@Controller("/instagram")
class LoginController(
    private val keycloakService: KeycloakServicePort
) {

    @Post("/login")
    @Secured(SecurityRule.IS_ANONYMOUS)
    fun login(@Body request: LoginRequest): MutableHttpResponse<String>? {
        val token: String = keycloakService.login(
            LoginConverter.loguinRequestToUserLoginDomain(request)
        )
        return HttpResponse.ok(token)
    }

//    @Post("login")
//    @Secured(SecurityRule.IS_ANONYMOUS)
//    fun loginAccount(@Body user: LoginRequest): HttpResponse<String>{
//        val result = keyclockLoginSevicePort.getTokenUser(user)
//        return HttpResponse.ok(result).status(200).body(result)
//    }

    @Get("/admin")
    @Secured("admin")
    fun admin(authentication: Authentication): MutableHttpResponse<String>? {
        return HttpResponse.ok("Olá mundo ${authentication.roles}")
    }
}