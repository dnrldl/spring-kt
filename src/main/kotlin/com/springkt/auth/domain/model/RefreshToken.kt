package com.springkt.auth.domain.model

import java.time.Instant

data class RefreshToken(
    val userId: Long,
    val token: String,
    val expiresAt: Instant,
)
