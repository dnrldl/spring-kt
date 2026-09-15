package com.springkt.auth.presentation.dto

import com.springkt.auth.application.usecase.LoginResult

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "DPoP",
) {
    companion object {
        fun from(result: LoginResult): TokenResponse = TokenResponse(
            accessToken = result.accessToken,
            refreshToken = result.refreshToken,
        )
    }
}
