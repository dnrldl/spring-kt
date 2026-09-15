package com.springkt.global.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret:change-me-change-me-change-me-change-me-change-me-change-me}")
    private val secret: String,
    @Value("\${jwt.access-token-validity-seconds:3600}")
    private val accessTokenValiditySeconds: Long,
    @Value("\${jwt.refresh-token-validity-seconds:1209600}")
    val refreshTokenValiditySeconds: Long,
) {
    private val signingKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun issueAccessToken(
        userId: Long,
        email: String,
        role: String,
        dpopKeyThumbprint: String,
    ): String {
        val now = Instant.now()
        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("role", role)
            .claim("cnf", mapOf("jkt" to dpopKeyThumbprint))
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(accessTokenValiditySeconds)))
            .signWith(signingKey)
            .compact()
    }

    fun issueRefreshToken(userId: Long): String {
        val now = Instant.now()
        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(refreshTokenValiditySeconds)))
            .signWith(signingKey)
            .compact()
    }

    fun validate(token: String): Boolean {
        return try {
            parseClaims(token)
            true
        } catch (_: JwtException) {
            false
        } catch (_: IllegalArgumentException) {
            false
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)
        val role = requireNotNull(claims["role"] as? String)
        val principal = JwtPrincipal(
            userId = requireNotNull(claims.subject).toLong(),
            email = requireNotNull(claims["email"] as? String),
        )
        val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))

        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun getDpopKeyThumbprint(token: String): String? {
        val claims = parseClaims(token)
        val confirmation = claims["cnf"] as? Map<*, *> ?: return null
        return confirmation["jkt"] as? String
    }

    private fun parseClaims(token: String) = Jwts.parser()
        .verifyWith(signingKey)
        .build()
        .parseSignedClaims(token)
        .payload
}
