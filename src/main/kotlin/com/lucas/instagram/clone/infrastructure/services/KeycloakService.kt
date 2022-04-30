package com.lucas.instagram.clone.infrastructure.services

import com.google.gson.JsonParser
import com.lucas.instagram.clone.core.config.KeycloakConfig
import com.lucas.instagram.clone.core.model.TokenVerified
import com.lucas.instagram.clone.core.model.UserLoginDomain
import com.lucas.instagram.clone.core.model.UserRegisterDomain
import com.lucas.instagram.clone.core.ports.KeycloakCacheServicePort
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
import java.util.UUID

@Prototype
class KeycloakService(
    private val keycloakConfig: KeycloakConfig,
    private val keycloakCacheService: KeycloakCacheServicePort
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

    override fun registerUser(user: UserRegisterDomain): String? {
//        logger().info("signUp - Inicio do serviÃ§o keycloak")
        val tokenAdminCliCache = keycloakCacheService.readTokenAdminCliCache()

//        logger().info("signUp - Verificando o Token do Admin Cli (CACHE)")
        val accessToken = if (tokenAdminCliCache != null && this.verifyExpTokenAdminCli(tokenAdminCliCache)) {
//            logger().info("signUp - Token do admin cli (cache) Ã© vÃ¡lido")
            tokenAdminCliCache
        } else {
//            logger().info("signUp - Token do admin cli (cache) invÃ¡lido, gerando um novo...")
            keycloakCacheService.deleteTokenAdminCliCache()

            val accessTokenAdminCli = this.getAccessTokenAdminCli()
            keycloakCacheService.saveTokenAdminCliCache(accessTokenAdminCli)

            accessTokenAdminCli
        }

        val mediaType = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, "{\"username\":\"${user.username}\"," +
                "\"credentials\":[{\"type\":\"password\",\"value\":\"${user.password}\",\"temporary\":false}],\"enabled\":true}," +
                "\"realmRoles\":[admin]\"")
        val request = Request.Builder()
            .url(keycloakConfig.usersRegisterUrl)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw RuntimeException("KeycloakSingUpService - Não foi possivel cadastrar o usuario")
        }

        val responseSplited = response.headers().get("Location").split("/")

        this.addRoleToUser(UUID.fromString(responseSplited[responseSplited.size-1]))

        return responseSplited[responseSplited.size-1]
    }


    private fun getAccessTokenAdminCli(): String {
//        logger().info("getAccessTokenAdminCli - capturando o access_token do admin-cli")

        val mediaType = MediaType.parse("application/x-www-form-urlencoded")
        val body = RequestBody.create(mediaType,
            "grant_type=client_credentials&client_id=admin-cli&client_secret=${keycloakConfig.clientSecretAdminCli}")
        val request = Request.Builder()
            .url(keycloakConfig.loginUrl)
            .post(body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        val response = client.newCall(request).execute()

        val responseBodyToString = response.body().string()
        val responseBodyToJson = JsonParser.parseString(responseBodyToString)
        if (responseBodyToString.contains("access_token")) {
//            logger().info("getAccessTokenAdminCli - access_token capturado!")
            return responseBodyToJson.asJsonObject["access_token"].asString
        } else {
            throw RuntimeException("KeycloakSingUpService - Token not received")
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

    private fun verifyExpTokenAdminCli(token: String): Boolean {
//        logger().info("verifyExpTokenAdminCli - verificando expiraÃ§Ã£o do token do admin-cli")

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

            return Instant.now().isAfter(expirationInstant)

        } catch (e: Exception) {
//            logger().error("verifyExpTokenAdminCli - token expirado ou invÃ¡lido")
            return false
        }
    }

    private fun addRoleToUser(userId: UUID){

        val tokenAdminCliCache = keycloakCacheService.readTokenAdminCliCache()

//        logger().info("signUp - Verificando o Token do Admin Cli (CACHE)")
        val accessToken = if (tokenAdminCliCache != null && this.verifyExpTokenAdminCli(tokenAdminCliCache)) {
//            logger().info("signUp - Token do admin cli (cache) Ã© vÃ¡lido")
            tokenAdminCliCache
        } else {
//            logger().info("signUp - Token do admin cli (cache) invÃ¡lido, gerando um novo...")
            keycloakCacheService.deleteTokenAdminCliCache()

            val accessTokenAdminCli = this.getAccessTokenAdminCli()
            keycloakCacheService.saveTokenAdminCliCache(accessTokenAdminCli)

            accessTokenAdminCli
        }

        val mediaType = MediaType.parse("application/json")
        val body = RequestBody.create(mediaType, "[\n{\n\"id\":\"107d23ad-f46a-495c-8230-d5b89d8e5e31\",\n\t\t\"name\":\"user\"\n}\n]")
        val request = Request.Builder()
            .url("${keycloakConfig.usersRegisterUrl}/${userId.toString()}/role-mappings/realm")
            .post(body)
            .addHeader("authorization", "bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .build()

        val response = client.newCall(request).execute()
    }
}