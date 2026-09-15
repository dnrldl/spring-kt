package com.springkt.auth.domain.repository

import com.springkt.auth.domain.model.RefreshToken

interface RefreshTokenRepository {
    fun save(refreshToken: RefreshToken)

    fun findByUserId(userId: Long): RefreshToken?

    fun deleteByUserId(userId: Long)
}
