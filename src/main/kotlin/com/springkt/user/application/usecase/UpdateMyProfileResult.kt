package com.springkt.user.application.usecase

import com.springkt.user.domain.model.UserRole
import com.springkt.user.domain.model.UserStatus

data class UpdateMyProfileResult(
    val id: Long,
    val email: String,
    val nickname: String,
    val role: UserRole,
    val status: UserStatus,
    val bio: String?,
    val profileImageUrl: String?,
)
