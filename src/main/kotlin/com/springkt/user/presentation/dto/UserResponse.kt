package com.springkt.user.presentation.dto

import com.springkt.user.application.usecase.RegisterUserResult

data class UserResponse(
    val id: Long,
    val email: String,
    val nickname: String,
) {
    companion object {
        fun from(result: RegisterUserResult): UserResponse = UserResponse(
            id = result.id,
            email = result.email,
            nickname = result.nickname,
        )
    }
}
