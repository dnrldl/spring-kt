package com.springkt.user.domain.service

import com.springkt.global.error.BusinessException
import com.springkt.global.error.ErrorCode
import com.springkt.user.domain.model.User
import com.springkt.user.domain.model.UserStatus
import com.springkt.user.domain.repository.UserRepository
import com.springkt.user.domain.repository.UserProfileRepository
import org.springframework.stereotype.Component

@Component
class UserValidator(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository,
) {
    fun register(
        email: String,
        nickname: String,
    ) {
        if (userRepository.existsByEmail(email)) {
            throw BusinessException(ErrorCode.DUPLICATED_EMAIL)
        }
        if (userProfileRepository.existsByNickname(nickname)) {
            throw BusinessException(ErrorCode.DUPLICATED_NICKNAME)
        }
    }

    fun reactivate(
        user: User,
        nickname: String,
    ) {
        if (user.status != UserStatus.WITHDRAWN) {
            throw BusinessException(ErrorCode.DUPLICATED_EMAIL)
        }

        val userId = requireNotNull(user.id)
        if (userProfileRepository.existsByNicknameAndUserIdNot(nickname, userId)) {
            throw BusinessException(ErrorCode.DUPLICATED_NICKNAME)
        }
    }

    fun updateMyProfile(
        userId: Long,
        nickname: String?,
    ) {
        if (nickname != null && userProfileRepository.existsByNicknameAndUserIdNot(nickname, userId)) {
            throw BusinessException(ErrorCode.DUPLICATED_NICKNAME)
        }
    }
}
