package com.springkt.user.application.usecase

data class RegisterUserResult(
    val id: Long,
    val email: String,
    val nickname: String,
)
