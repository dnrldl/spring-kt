package com.springkt.global.security

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.OctetKeyPair
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory
import com.nimbusds.jwt.SignedJWT
import com.springkt.global.error.BusinessException
import com.springkt.global.error.ErrorCode
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Duration
import java.time.Instant
import java.util.Date

@Component
class DpopProofValidator(
    private val stringRedisTemplate: StringRedisTemplate,
) {
    fun validateTokenEndpointProof(
        request: HttpServletRequest,
        proofJwt: String,
    ): String {
        val proof = parseProof(proofJwt)
        val keyThumbprint = verifyProofSignature(proof)
        verifyCommonClaims(
            proof = proof,
            request = request,
            keyThumbprint = keyThumbprint,
        )
        return keyThumbprint
    }

    fun validateResourceProof(
        request: HttpServletRequest,
        proofJwt: String,
        accessToken: String,
        expectedKeyThumbprint: String,
    ) {
        val proof = parseProof(proofJwt)
        val keyThumbprint = verifyProofSignature(proof)

        if (keyThumbprint != expectedKeyThumbprint) {
            throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
        }

        verifyCommonClaims(
            proof = proof,
            request = request,
            keyThumbprint = keyThumbprint,
        )

        if (proof.jwtClaimsSet.getStringClaim(ATH_CLAIM) != accessTokenHash(accessToken)) {
            throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
        }
    }

    private fun parseProof(proofJwt: String): SignedJWT {
        return try {
            SignedJWT.parse(proofJwt)
        } catch (_: Exception) {
            throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
        }
    }

    private fun verifyProofSignature(proof: SignedJWT): String {
        val header = proof.header
        val jwk = header.jwk ?: throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)

        if (header.type?.type != DPOP_JWT_TYPE || header.algorithm == JWSAlgorithm.NONE) {
            throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
        }

        if (jwk.isPrivate) {
            throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
        }

        val verifier = try {
            DefaultJWSVerifierFactory().createJWSVerifier(header, jwk.toSupportedPublicKey())
        } catch (_: JOSEException) {
            throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
        }

        if (!proof.verify(verifier)) {
            throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
        }

        return jwk.computeThumbprint().toString()
    }

    private fun verifyCommonClaims(
        proof: SignedJWT,
        request: HttpServletRequest,
        keyThumbprint: String,
    ) {
        val claims = proof.jwtClaimsSet
        val jti = claims.jwtid ?: throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
        val htm = claims.getStringClaim(HTM_CLAIM) ?: throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
        val htu = claims.getStringClaim(HTU_CLAIM) ?: throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
        val issueTime = claims.issueTime ?: throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)

        if (htm != request.method.uppercase()) {
            throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
        }

        if (htu != request.requestURL.toString()) {
            throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
        }

        if (!isAcceptableIssueTime(issueTime)) {
            throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
        }

        preventReplay(
            keyThumbprint = keyThumbprint,
            jti = jti,
        )
    }

    private fun isAcceptableIssueTime(issueTime: Date): Boolean {
        val now = Instant.now()
        val issuedAt = issueTime.toInstant()
        return !issuedAt.isBefore(now.minus(PROOF_VALIDITY)) && !issuedAt.isAfter(now.plus(CLOCK_SKEW))
    }

    private fun preventReplay(
        keyThumbprint: String,
        jti: String,
    ) {
        val key = "dpop:jti:$keyThumbprint:$jti"
        val stored = stringRedisTemplate.opsForValue()
            .setIfAbsent(key, "1", PROOF_VALIDITY)

        if (stored != true) {
            throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
        }
    }

    private fun accessTokenHash(accessToken: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(accessToken.toByteArray(StandardCharsets.US_ASCII))
        return Base64Url.encode(digest)
    }

    private object Base64Url {
        fun encode(bytes: ByteArray): String {
            return java.util.Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes)
        }
    }

    private fun JWK.toSupportedPublicKey() = when (val publicJwk = toPublicJWK()) {
        is RSAKey -> publicJwk.toPublicKey()
        is ECKey -> publicJwk.toPublicKey()
        is OctetKeyPair -> publicJwk.toPublicKey()
        else -> throw BusinessException(ErrorCode.INVALID_DPOP_PROOF)
    }

    private companion object {
        const val DPOP_JWT_TYPE = "dpop+jwt"
        const val HTM_CLAIM = "htm"
        const val HTU_CLAIM = "htu"
        const val ATH_CLAIM = "ath"
        val PROOF_VALIDITY: Duration = Duration.ofMinutes(5)
        val CLOCK_SKEW: Duration = Duration.ofSeconds(30)
    }
}
