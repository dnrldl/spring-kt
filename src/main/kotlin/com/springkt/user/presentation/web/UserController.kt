package com.springkt.user.presentation.web

import com.springkt.global.security.JwtPrincipal
import com.springkt.global.web.SuccessResponse
import com.springkt.user.application.usecase.GetMyProfileUseCase
import com.springkt.user.application.usecase.RegisterUserUseCase
import com.springkt.user.presentation.dto.RegisterUserRequest
import com.springkt.user.presentation.dto.UserProfileResponse
import com.springkt.user.presentation.dto.UserResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "유저 API")
class UserController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
) {
    @PostMapping
    @Operation(summary = "회원가입", description = "일반 유저를 생성합니다.")
    fun register(
        @Valid @RequestBody request: RegisterUserRequest,
    ): SuccessResponse<UserResponse> {
        val result = registerUserUseCase.register(request.toCommand())
        return SuccessResponse.ok(UserResponse.from(result))
    }

    @GetMapping("/me")
    @Operation(
        summary = "내 정보 조회",
        description = "DPoP access token으로 현재 로그인한 유저 정보를 조회합니다.",
        security = [
            SecurityRequirement(name = "dpopAuth"),
            SecurityRequirement(name = "dpopProof"),
        ],
    )
    fun getMyProfile(
        @Parameter(hidden = true)
        @AuthenticationPrincipal principal: JwtPrincipal,
    ): SuccessResponse<UserProfileResponse> {
        val result = getMyProfileUseCase.getMyProfile(principal.userId)
        return SuccessResponse.ok(UserProfileResponse.from(result))
    }
}
