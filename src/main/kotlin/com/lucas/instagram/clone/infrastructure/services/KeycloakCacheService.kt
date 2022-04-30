package com.lucas.instagram.clone.infrastructure.services

import com.lucas.instagram.clone.core.ports.KeycloakCacheServicePort
import io.lettuce.core.RedisClient
import jakarta.inject.Singleton
import java.time.Duration

@Singleton
class KeycloakCacheService(
    private var redisClient: RedisClient
): KeycloakCacheServicePort{
    private var connection = redisClient.connect()
    private var commands = connection.sync()

    override fun saveTokenAdminCliCache(tokenAdminCli: String) {
        commands.set("tokenAdminCli", tokenAdminCli)
        commands.expire("tokenAdminCli", Duration.ofMinutes(40))
    }

    override fun readTokenAdminCliCache(): String? {
        return commands.get("tokenAdminCli")
    }

    override fun deleteTokenAdminCliCache(): Long? {
        return commands.del("tokenAdminCli")
    }
}