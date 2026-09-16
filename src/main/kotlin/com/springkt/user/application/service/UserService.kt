package com.springkt.user.application.service

import com.springkt.global.error.BusinessException
import com.springkt.global.error.ErrorCode
import com.springkt.user.application.usecase.GetMyProfileResult
import com.springkt.user.application.usecase.GetMyProfileUseCase
import com.springkt.user.application.usecase.RegisterUserCommand
import com.springkt.user.application.usecase.RegisterUserResult
import com.springkt.user.application.usecase.RegisterUserUseCase
import com.springkt.user.application.usecase.UpdateMyProfileCommand
import com.springkt.user.application.usecase.UpdateMyProfileResult
import com.springkt.user.application.usecase.UpdateMyProfileUseCase
import com.springkt.user.application.usecase.toGetMyProfileResult
import com.springkt.user.domain.model.User
import com.springkt.user.domain.model.UserProfile
import com.springkt.user.domain.repository.UserProfileRepository
import com.springkt.user.domain.repository.UserQueryRepository
import com.springkt.user.domain.repository.UserRepository
import com.springkt.user.domain.service.UserValidator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userProfileRepository: UserProfileRepository,
    private val userQueryRepository: UserQueryRepository,
    private val userValidator: UserValidator,
    private val passwordEncoder: PasswordEncoder,
) : RegisterUserUseCase, GetMyProfileUseCase, UpdateMyProfileUseCase {

    @Transactional(readOnly = true)
    override fun getMyProfile(userId: Long): GetMyProfileResult {
        val result = (userQueryRepository.findProfileViewById(userId)?.toGetMyProfileResult()
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND))
        return result
    }

    @Transactional
    override fun register(command: RegisterUserCommand): RegisterUserResult {
        userValidator.register(
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
            )
        )

        val saveUserProfile = userProfileRepository.save(
            UserProfile.register(
                userId = requireNotNull(saveUser.id),
                nickname = command.nickname,
                bio = command.bio,
                profileImageUrl = command.profileImageUrl
            )
        )

        return saveUser.toResult(saveUserProfile)
    }

    @Transactional
    override fun updateMyProfile(command: UpdateMyProfileCommand): UpdateMyProfileResult {
        val user = userRepository.findById(command.userId)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        val userProfile = userProfileRepository.findByUserId(command.userId)
            ?: throw BusinessException(ErrorCode.USER_PROFILE_NOT_FOUND)

        userValidator.updateMyProfile(command.userId, command.nickname)

        val updatedUserProfile = userProfile.update(
            nickname = command.nickname,
            bio = command.bio,
            profileImageUrl = command.profileImageUrl,
        )

        val savedUserProfile = userProfileRepository.save(updatedUserProfile)

        return savedUserProfile.toResult(user)
    }

    private fun User.toResult(userProfile: UserProfile): RegisterUserResult = RegisterUserResult(
        id = requireNotNull(id),
        email = email,
        nickname = userProfile.nickname,
    )

    private fun UserProfile.toResult(user: User): UpdateMyProfileResult =
        UpdateMyProfileResult(
            id = userId,
            email = user.email,
            nickname = nickname,
            role = user.role,
            status = user.status,
            bio = bio,
            profileImageUrl = profileImageUrl,
        )

}
