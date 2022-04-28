package com.lucas.instagram.clone.infrastructure.services

import com.google.gson.JsonParser
import com.lucas.instagram.clone.core.config.KeycloakConfig
import com.lucas.instagram.clone.core.model.TokenVerified
import com.lucas.instagram.clone.core.model.UserLoginDomain
import com.lucas.instagram.clone.core.ports.OpenIdServicePort
import com.nimbusds.jose.jwk.JWK
import com.squareup.okhttp.MediaType
import io.micronaut.context.annotation.Prototype
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import io.jsonwebtoken.Jwts
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.time.Instant

@Prototype
class KeycloakService(
    private val keycloakConfig: KeycloakConfig
): OpenIdServicePort {

    private val client = OkHttpClient()

    override fun login(userLogin: UserLoginDomain): String {

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

    override fun verifyToken(token: String): TokenVerified {
        try {
            val jwk = JWK.parse(this.getJWKCerts())

            val rsaKey = jwk.toRSAKey()

            val factory = KeyFactory.getInstance(jwk.keyType.value)
            val rsaPublicKeySpec = RSAPublicKeySpec(
                rsaKey.modulus.decodeToBigInteger(), // n
                rsaKey.publicExponent.decodeToBigInteger() //e
            )
            val publicKeySpec: PublicKey = factory.generatePublic(rsaPublicKeySpec)

            val claims = Jwts.parser().setSigningKey(publicKeySpec).parseClaimsJws(token)

            val expiration = claims.body["exp"] as Int
            val expirationInstant = Instant.ofEpochMilli(expiration.toLong())

            return TokenVerified(valid = Instant.now().isAfter(expirationInstant))

        } catch (e: Exception){
            println(e.message)
            if(e.message.equals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.")){
                return TokenVerified(valid = false, reason = "Invalid token")
            }

            return TokenVerified(valid = false, reason = "Expired token")

        }
    }

    private fun getJWKCerts(): String{
        // Obtendo o certificado para gerar a public key
        val request = Request.Builder()
            .url(keycloakConfig.certsRSAUrl)
            .get()
            .build()

        try{
            val response = client.newCall(request).execute()
            val responseJson = JsonParser.parseString(response.body().string())

            val keys = responseJson.asJsonObject["keys"].asJsonArray

            return keys[1].toString()
        }catch(e: Exception){
            println("erro") // FIXME
            throw RuntimeException("Erro")
        }
    }
}