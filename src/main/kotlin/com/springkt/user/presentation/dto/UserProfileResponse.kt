package com.springkt.user.presentation.dto

import com.springkt.user.application.usecase.GetMyProfileResult

data class UserProfileResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val role: String,
    val status: String,
    val bio: String?,
    val profileImageUrl: String?,
) {
    companion object {
        fun from(result: GetMyProfileResult): UserProfileResponse = UserProfileResponse(
            id = result.id,
            email = result.email,
            nickname = result.nickname,
            role = result.role.name,
            status = result.status.name,
            bio = result.bio,
            profileImageUrl = result.profileImageUrl,
        )
    }
}
