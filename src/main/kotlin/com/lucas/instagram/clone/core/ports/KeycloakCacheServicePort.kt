package com.lucas.instagram.clone.core.ports

interface KeycloakCacheServicePort {
    fun saveTokenAdminCliCache(tokenAdminCli: String)

    fun readTokenAdminCliCache(): String?

    fun deleteTokenAdminCliCache(): Long?
}