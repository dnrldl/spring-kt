package com.springkt.user.infrastructure.persistence

import com.springkt.global.error.BusinessException
import com.springkt.global.error.ErrorCode
import com.springkt.user.domain.model.UserProfile
import com.springkt.user.domain.repository.UserProfileRepository
import org.springframework.stereotype.Repository

@Repository
class UserProfilePersistenceAdapter(
    private val springDataUserJpaRepository: SpringDataUserJpaRepository,
    private val springDataUserProfileJpaRepository: SpringDataUserProfileJpaRepository,
) : UserProfileRepository {

    override fun save(userProfile: UserProfile): UserProfile {
        if (!springDataUserJpaRepository.existsById(userProfile.userId)) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }

        return springDataUserProfileJpaRepository
            .save(UserProfileJpaEntity.from(userProfile))
            .toDomain()
    }

    override fun findByUserId(userId: Long): UserProfile? {
        return springDataUserProfileJpaRepository.findById(userId)
            .map(UserProfileJpaEntity::toDomain)
            .orElse(null)
    }

    override fun existsByNickname(nickname: String): Boolean {
        return springDataUserProfileJpaRepository.existsByNickname(nickname.trim())
    }
}
