package com.springkt.user.domain.model

data class UserProfile(
    val userId: Long,
    val nickname: String,
    val bio: String?,
    val profileImageUrl: String?,
) {
    companion object {
        fun create(
            userId: Long,
            nickname: String,
            bio: String? = null,
            profileImageUrl: String? = null,
        ): UserProfile = UserProfile(
            userId = userId,
            nickname = nickname.trim(),
            bio = bio?.trim()?.takeIf { it.isNotBlank() },
            profileImageUrl = profileImageUrl?.trim()?.takeIf { it.isNotBlank() },
        )

        fun reconstruct(
            userId: Long,
            nickname: String,
            bio: String?,
            profileImageUrl: String?,
        ): UserProfile = UserProfile(
            userId = userId,
            nickname = nickname,
            bio = bio,
            profileImageUrl = profileImageUrl,
        )
    }
}
