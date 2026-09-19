package com.springkt.user.presentation.web

import com.springkt.global.security.CurrentUserId
import com.springkt.global.web.SuccessResponse
import com.springkt.user.application.usecase.GetMyProfileUseCase
import com.springkt.user.application.usecase.RegisterUserUseCase
import com.springkt.user.application.usecase.UpdateMyProfileUseCase
import com.springkt.user.application.usecase.WithdrawUserUseCase
import com.springkt.user.presentation.dto.RegisterUserRequest
import com.springkt.user.presentation.dto.UpdateMyProfileRequest
import com.springkt.user.presentation.dto.UserProfileResponse
import com.springkt.user.presentation.dto.UserResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "유저 API")
class UserController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val updateMyProfileUseCase: UpdateMyProfileUseCase,
    private val withdrawUserUseCase: WithdrawUserUseCase,
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
        @CurrentUserId userId: Long,
    ): SuccessResponse<UserProfileResponse> {
        val result = getMyProfileUseCase.getMyProfile(userId)
        return SuccessResponse.ok(UserProfileResponse.from(result))
    }

    @PatchMapping("/me")
    @Operation(
        summary = "내 정보 수정",
        description = "DPoP access token으로 현재 로그인한 유저 정보를 수정합니다.",
        security = [
            SecurityRequirement(name = "dpopAuth"),
            SecurityRequirement(name = "dpopProof"),
        ],
    )
    fun updateMyProfile(
        @CurrentUserId userId: Long,
        @Valid @RequestBody request: UpdateMyProfileRequest
    ): SuccessResponse<UserProfileResponse> {
        val result = updateMyProfileUseCase.updateMyProfile(request.toCommand(userId))
        return SuccessResponse.ok(UserProfileResponse.from(result))
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "회원 탈퇴",
        description = "DPoP access token으로 현재 로그인한 유저를 탈퇴 처리합니다.",
        security = [
            SecurityRequirement(name = "dpopAuth"),
            SecurityRequirement(name = "dpopProof"),
        ],
    )
    fun withdraw(
        @CurrentUserId userId: Long,
    ) {
        withdrawUserUseCase.withdraw(userId)
    }
}
