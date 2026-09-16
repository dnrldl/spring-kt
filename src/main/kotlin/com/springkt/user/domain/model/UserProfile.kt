package com.springkt.user.domain.model

data class UserProfile(
    val userId: Long,
    val nickname: String,
    val bio: String?,
    val profileImageUrl: String?,
) {
    fun update(
        nickname: String?,
        bio: String?,
        profileImageUrl: String?,
    ): UserProfile = copy(
        nickname = nickname?.trim()?.takeIf { it.isNotBlank() } ?: this.nickname,
        bio = bio?.trim()?.takeIf { it.isNotBlank() } ?: this.bio,
        profileImageUrl = profileImageUrl?.trim()?.takeIf { it.isNotBlank() } ?: this.profileImageUrl,
    )

    companion object {
        fun register(
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
