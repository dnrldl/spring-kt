package com.springkt.global.security

data class JwtPrincipal(
    val userId: Long,
    val email: String,
)
