package com.springkt.user.application.service

import com.springkt.global.error.BusinessException
import com.springkt.global.error.ErrorCode
import com.springkt.user.application.usecase.GetMyProfileResult
import com.springkt.user.application.usecase.GetMyProfileUseCase
import com.springkt.user.application.usecase.RegisterUserCommand
import com.springkt.user.application.usecase.RegisterUserResult
import com.springkt.user.application.usecase.RegisterUserUseCase
import com.springkt.user.application.usecase.toGetMyProfileResult
import com.springkt.user.domain.model.User
import com.springkt.user.domain.model.UserProfile
import com.springkt.user.domain.repository.UserProfileRepository
import com.springkt.user.domain.repository.UserQueryRepository
import com.springkt.user.domain.repository.UserRepository
import com.springkt.user.domain.service.UserRegistrationValidator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository,
    private val userQueryRepository: UserQueryRepository,
    private val userRegistrationValidator: UserRegistrationValidator,
    private val passwordEncoder: PasswordEncoder,
) : RegisterUserUseCase, GetMyProfileUseCase {

    @Transactional
    override fun register(command: RegisterUserCommand): RegisterUserResult {
        userRegistrationValidator.validate(
            email = command.email,
            nickname = command.nickname,
        )
        val passwordHash = requireNotNull(passwordEncoder.encode(command.password)) {
            "비밀번호 암호화 결과가 비어있습니다."
        }

        val saveUser = userRepository.save(
            User.register(
                email = command.email,
                passwordHash = passwordHash,
                nickname = command.nickname,
            )
        )

        userProfileRepository.save(
            UserProfile.create(
                userId = requireNotNull(saveUser.id),
                nickname = command.nickname,
                bio = command.bio,
                profileImageUrl = command.profileImageUrl
            )
        )

        return saveUser.toRegisterUserResult()
    }

    @Transactional(readOnly = true)
    override fun getMyProfile(userId: Long): GetMyProfileResult {
        val result = (userQueryRepository.findProfileViewById(userId)?.toGetMyProfileResult()
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND))
        return result
    }

    private fun User.toRegisterUserResult(): RegisterUserResult = RegisterUserResult(
        id = requireNotNull(id),
        email = email,
        nickname = nickname,
    )

}
