package com.springkt.auth.presentation.web

import com.springkt.auth.application.usecase.LoginUseCase
import com.springkt.auth.presentation.dto.LoginRequest
import com.springkt.auth.presentation.dto.TokenResponse
import com.springkt.global.error.BusinessException
import com.springkt.global.error.ErrorCode
import com.springkt.global.security.DpopProofValidator
import com.springkt.global.web.SuccessResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
class AuthController(
    private val loginUseCase: LoginUseCase,
    private val dpopProofValidator: DpopProofValidator,
) {
    @PostMapping("/login")
    @Operation(
        summary = "로그인",
        description = "이메일/비밀번호와 DPoP proof로 로그인하고 DPoP access token 및 refresh token을 발급합니다.",
        parameters = [
            Parameter(
                name = DPOP_HEADER,
                `in` = ParameterIn.HEADER,
                required = true,
                description = "로그인 요청용 DPoP proof JWT",
            ),
        ],
    )
    fun login(
        @Valid @RequestBody request: LoginRequest,
        @Parameter(hidden = true)
        httpServletRequest: HttpServletRequest,
    ): SuccessResponse<TokenResponse> {
        val dpopProof = httpServletRequest.getHeader(DPOP_HEADER)
            ?: throw BusinessException(ErrorCode.DPOP_PROOF_REQUIRED)
        val dpopKeyThumbprint = dpopProofValidator.validateTokenEndpointProof(
            request = httpServletRequest,
            proofJwt = dpopProof,
        )
        val result = loginUseCase.login(request.toCommand(dpopKeyThumbprint))
        return SuccessResponse.ok(TokenResponse.from(result))
    }

    private companion object {
        const val DPOP_HEADER = "DPoP"
    }
}
