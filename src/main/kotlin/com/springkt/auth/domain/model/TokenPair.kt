package com.springkt.auth.domain.model

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
)
