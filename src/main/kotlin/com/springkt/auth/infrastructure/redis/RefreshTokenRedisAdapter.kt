package com.springkt.auth.infrastructure.redis

import com.springkt.auth.domain.model.RefreshToken
import com.springkt.auth.domain.repository.RefreshTokenRepository
import org.springframework.stereotype.Repository

@Repository
class RefreshTokenRedisAdapter(
    private val springDataRefreshTokenRedisRepository: SpringDataRefreshTokenRedisRepository,
) : RefreshTokenRepository {

    override fun save(refreshToken: RefreshToken) {
        springDataRefreshTokenRedisRepository.save(RefreshTokenRedisEntity.from(refreshToken))
    }

    override fun findByUserId(userId: Long): RefreshToken? {
        return springDataRefreshTokenRedisRepository.findById(userId).orElse(null)?.toDomain()
    }

    override fun deleteByUserId(userId: Long) {
        springDataRefreshTokenRedisRepository.deleteById(userId)
    }
}
