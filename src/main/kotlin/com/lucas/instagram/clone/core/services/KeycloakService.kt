package com.lucas.instagram.clone.core.services

import com.google.gson.JsonParser
import com.lucas.instagram.clone.core.config.KeycloakConfig
import com.lucas.instagram.clone.core.model.UserLoginDomain
import com.lucas.instagram.clone.core.ports.KeycloakServicePort
import com.squareup.okhttp.MediaType
import io.micronaut.context.annotation.Prototype
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody

@Prototype
class KeycloakService(
    private val keycloakConfig: KeycloakConfig
): KeycloakServicePort {
    override fun login(userLogin: UserLoginDomain): String {
        val client = OkHttpClient()

        val mediaType = MediaType.parse("application/x-www-form-urlencoded")
        val body = RequestBody.create(mediaType, "username=${userLogin.username}&password=${userLogin.password}&client_id=${keycloakConfig.clientId}&client_secret=${keycloakConfig.clientSecret}&grant_type=${keycloakConfig.grantType}")
        val request = Request.Builder()
            .url(keycloakConfig.loginUrl)
            .post(body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        val response = client.newCall(request).execute()

        val responseBodyToString = response.body().string()
        val responseBodyToJson = JsonParser.parseString(responseBodyToString)

        if (responseBodyToString.contains("access_token")) {
            return responseBodyToJson.asJsonObject["access_token"].asString
        } else {
            throw java.lang.RuntimeException("KeycloakSingUpService - Token not received")
        }
    }
}