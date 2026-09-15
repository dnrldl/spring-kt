package com.springkt.user.application.usecase

import com.springkt.user.domain.model.UserRole
import com.springkt.user.domain.model.UserStatus
import com.springkt.user.domain.query.UserProfileView

data class GetMyProfileResult(
    val id: Long,
    val email: String,
    val nickname: String,
    val role: UserRole,
    val status: UserStatus,
    val bio: String?,
    val profileImageUrl: String?,
)

fun UserProfileView.toGetMyProfileResult(): GetMyProfileResult = GetMyProfileResult(
    id = id,
    email = email,
    nickname = nickname,
    role = role,
    status = status,
    bio = bio,
    profileImageUrl = profileImageUrl,
)
