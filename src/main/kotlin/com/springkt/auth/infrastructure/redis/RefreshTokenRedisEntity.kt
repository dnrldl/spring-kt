package com.springkt.auth.infrastructure.redis

import com.springkt.auth.domain.model.RefreshToken
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import java.time.Duration
import java.time.Instant

@RedisHash("refresh_token")
class RefreshTokenRedisEntity(
    @Id
    var userId: Long = 0,

    var token: String = "",

    @TimeToLive
    var ttlSeconds: Long = 0,
) {
    fun toDomain(): RefreshToken = RefreshToken(
        userId = userId,
        token = token,
        expiresAt = Instant.now().plusSeconds(ttlSeconds),
    )

    companion object {
        fun from(refreshToken: RefreshToken): RefreshTokenRedisEntity {
            val ttlSeconds = Duration.between(Instant.now(), refreshToken.expiresAt).seconds.coerceAtLeast(0)
            return RefreshTokenRedisEntity(
                userId = refreshToken.userId,
                token = refreshToken.token,
                ttlSeconds = ttlSeconds,
            )
        }
    }
}
