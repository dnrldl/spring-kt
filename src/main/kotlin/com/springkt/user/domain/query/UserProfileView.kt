package com.springkt.user.domain.query

import com.springkt.user.domain.model.UserRole
import com.springkt.user.domain.model.UserStatus

data class UserProfileView(
    val id: Long,
    val email: String,
    val nickname: String,
    val role: UserRole,
    val status: UserStatus,
    val bio: String?,
    val profileImageUrl: String?,
)
