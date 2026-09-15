package com.springkt.auth.application.usecase

data class LoginCommand(
    val email: String,
    val password: String,
    val dpopKeyThumbprint: String,
)
