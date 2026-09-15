package com.springkt.user.application.usecase

class RegisterUserCommand private constructor(
    val email: String,
    val password: String,
    val nickname: String,
    val bio: String?,
    val profileImageUrl: String?,
) {
    companion object {
        fun of(
            email: String,
            password: String,
            nickname: String,
            bio: String?,
            profileImageUrl: String?,
        ): RegisterUserCommand = RegisterUserCommand(
            email = email.trim().lowercase(),
            password = password,
            nickname = nickname.trim(),
            bio = bio?.trim()?.takeIf { it.isNotBlank() },
            profileImageUrl = profileImageUrl?.trim()?.takeIf { it.isNotBlank() },
        )
    }
}
