package com.springkt.user.presentation.dto

import com.springkt.user.application.usecase.UpdateMyProfileCommand
import jakarta.validation.constraints.Size

data class UpdateMyProfileRequest(
    @field:Size(max = 30, message = "{validation.nickname.size}")
    val nickname: String?,

    @field:Size(max = 1000, message = "{validation.bio.size}")
    val bio: String?,

    val profileImageUrl: String?,
) {
    fun toCommand(userId: Long): UpdateMyProfileCommand = UpdateMyProfileCommand.of(
        userId = userId,
        nickname = nickname,
        bio = bio,
        profileImageUrl = profileImageUrl
    )
}
