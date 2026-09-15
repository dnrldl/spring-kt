package com.springkt.auth.domain.service

import com.springkt.auth.domain.model.RefreshToken
import com.springkt.auth.domain.model.TokenPair
import com.springkt.global.security.JwtTokenProvider
import com.springkt.user.domain.model.User
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class AuthTokenFactory(
    private val jwtTokenProvider: JwtTokenProvider,
) {
    fun issue(
        user: User,
        dpopKeyThumbprint: String,
    ): IssuedAuthToken {
        val userId = requireNotNull(user.id)
        val tokenPair = TokenPair(
            accessToken = jwtTokenProvider.issueAccessToken(
                userId = userId,
                email = user.email,
                role = user.role.name,
                dpopKeyThumbprint = dpopKeyThumbprint,
            ),
            refreshToken = jwtTokenProvider.issueRefreshToken(userId),
        )

        return IssuedAuthToken(
            tokenPair = tokenPair,
            refreshToken = RefreshToken(
                userId = userId,
                token = tokenPair.refreshToken,
                expiresAt = Instant.now().plusSeconds(jwtTokenProvider.refreshTokenValiditySeconds),
            ),
        )
    }
}

data class IssuedAuthToken(
    val tokenPair: TokenPair,
    val refreshToken: RefreshToken,
)
