package com.springkt.global.security

import com.springkt.user.domain.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val dpopProofValidator: DpopProofValidator,
    private val userRepository: UserRepository,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = resolveDpopAccessToken(request)
        val proof = request.getHeader(DPOP_HEADER)

        if (token != null && proof != null && SecurityContextHolder.getContext().authentication == null) {
            if (jwtTokenProvider.validate(token) && validateDpopProof(request, proof, token)) {
                val authentication = jwtTokenProvider.getAuthentication(token)
                if (isActiveUser(authentication)) {
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun isActiveUser(authentication: Authentication): Boolean {
        val principal = authentication.principal as? JwtPrincipal ?: return false
        return userRepository.findById(principal.userId)?.isActiveStatus() == true
    }

    private fun resolveDpopAccessToken(request: HttpServletRequest): String? {
        val authorization = request.getHeader(AUTHORIZATION_HEADER) ?: return null
        if (!authorization.startsWith(DPOP_AUTHORIZATION_PREFIX)) {
            return null
        }

        return authorization.removePrefix(DPOP_AUTHORIZATION_PREFIX).trim()
    }

    private fun validateDpopProof(
        request: HttpServletRequest,
        proof: String,
        accessToken: String,
    ): Boolean {
        val keyThumbprint = jwtTokenProvider.getDpopKeyThumbprint(accessToken) ?: return false
        return try {
            dpopProofValidator.validateResourceProof(
                request = request,
                proofJwt = proof,
                accessToken = accessToken,
                expectedKeyThumbprint = keyThumbprint,
            )
            true
        } catch (_: RuntimeException) {
            false
        }
    }

    private companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
        const val DPOP_HEADER = "DPoP"
        const val DPOP_AUTHORIZATION_PREFIX = "DPoP "
    }
}
