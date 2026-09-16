package com.springkt.user.domain.repository

import com.springkt.user.domain.model.UserProfile

interface UserProfileRepository {
    fun save(userProfile: UserProfile): UserProfile

    fun findByUserId(userId: Long): UserProfile?

    fun existsByNickname(nickname: String): Boolean

    fun existsByNicknameAndUserIdNot(nickname: String, userId: Long): Boolean
}
