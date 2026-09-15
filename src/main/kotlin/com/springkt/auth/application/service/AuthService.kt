package com.springkt.auth.application.service

import com.springkt.auth.application.usecase.LoginCommand
import com.springkt.auth.application.usecase.LoginResult
import com.springkt.auth.application.usecase.LoginUseCase
import com.springkt.auth.domain.repository.RefreshTokenRepository
import com.springkt.auth.domain.service.AuthTokenFactory
import com.springkt.global.error.BusinessException
import com.springkt.global.error.ErrorCode
import com.springkt.user.domain.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authTokenFactory: AuthTokenFactory,
    private val passwordEncoder: PasswordEncoder,
) : LoginUseCase {

    @Transactional
    override fun login(command: LoginCommand): LoginResult {
        val user = userRepository.findByEmail(command.email)
            ?: throw BusinessException(ErrorCode.INVALID_CREDENTIALS)

        if (!user.matchesPassword({ raw, encoded -> passwordEncoder.matches(raw, encoded) }, command.password)) {
            throw BusinessException(ErrorCode.INVALID_CREDENTIALS)
        }

        val issuedAuthToken = authTokenFactory.issue(
            user = user,
            dpopKeyThumbprint = command.dpopKeyThumbprint,
        )
        refreshTokenRepository.save(issuedAuthToken.refreshToken)

        return LoginResult(
            accessToken = issuedAuthToken.tokenPair.accessToken,
            refreshToken = issuedAuthToken.tokenPair.refreshToken,
        )
    }
}
