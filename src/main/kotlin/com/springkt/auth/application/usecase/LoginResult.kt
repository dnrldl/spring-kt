package com.springkt.auth.application.usecase

data class LoginResult(
    val accessToken: String,
    val refreshToken: String,
)
