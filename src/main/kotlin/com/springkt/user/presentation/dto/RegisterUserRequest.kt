package com.springkt.user.presentation.dto

import com.springkt.user.application.usecase.RegisterUserCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterUserRequest(
    @field:Email(message = "{validation.email.invalid}")
    @field:NotBlank(message = "{validation.email.required}")
    val email: String,

    @field:NotBlank(message = "{validation.password.required}")
    @field:Size(min = 8, max = 64, message = "{validation.password.size}")
    val password: String,

    @field:NotBlank(message = "{validation.nickname.required}")
    @field:Size(max = 30, message = "{validation.nickname.size}")
    val nickname: String,

    @field:Size(max = 1000, message = "{validation.bio.size}")
    val bio: String?,

    val profileImageUrl: String?
) {
    fun toCommand(): RegisterUserCommand = RegisterUserCommand.of(
        email = email,
        password = password,
        nickname = nickname,
        bio = bio,
        profileImageUrl = profileImageUrl,
    )
}
