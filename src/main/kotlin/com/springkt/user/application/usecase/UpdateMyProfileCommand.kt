package com.springkt.user.application.usecase

class UpdateMyProfileCommand private constructor(
    val userId: Long,
    val nickname: String?,
    val bio: String?,
    val profileImageUrl: String?,
) {
    companion object {
        fun of(
            userId: Long,
            nickname: String?,
            bio: String?,
            profileImageUrl: String?
        ): UpdateMyProfileCommand = UpdateMyProfileCommand(
            userId = userId,
            nickname = nickname?.trim()?.takeIf { it.isNotBlank() },
            bio = bio?.trim()?.takeIf { it.isNotBlank() },
            profileImageUrl = profileImageUrl?.trim()?.takeIf { it.isNotBlank() }
        )
    }
}
