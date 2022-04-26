package com.lucas.instagram.clone.core.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requires

@Factory
@ConfigurationProperties(KeycloakConfig.KEYCLOAK_CONF_PREFIX)
@Requires(KeycloakConfig.KEYCLOAK_CONF_PREFIX)
class KeycloakConfig {
    var usersRegisterUrl: String = ""
    var loginUrl: String = ""
    var grantType: String = ""
    var clientId: String = ""
    var clientSecret: String = ""
    var certsRSAUrl: String = ""
    var authUrl: String = ""

    companion object {
        const val KEYCLOAK_CONF_PREFIX = "keycloak"
    }
}