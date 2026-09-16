package com.springkt.user.domain.service

import com.springkt.global.error.BusinessException
import com.springkt.global.error.ErrorCode
import com.springkt.user.domain.repository.UserProfileRepository
import com.springkt.user.domain.repository.UserRepository
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

    fun updateMyProfile(
        userId: Long,
        nickname: String?,
    ) {
        if (nickname != null && userProfileRepository.existsByNicknameAndUserIdNot(nickname, userId)) {
            throw BusinessException(ErrorCode.DUPLICATED_NICKNAME)
        }
    }
}
