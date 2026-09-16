package com.springkt.user.application.service

import com.springkt.auth.domain.repository.RefreshTokenRepository
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
import com.springkt.user.application.usecase.WithdrawUserUseCase
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
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
) : RegisterUserUseCase,
    GetMyProfileUseCase,
    UpdateMyProfileUseCase,
    WithdrawUserUseCase {

    @Transactional(readOnly = true)
    override fun getMyProfile(userId: Long): GetMyProfileResult {
        val result = userQueryRepository.findProfileViewById(userId)?.toGetMyProfileResult()
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        return result
    }

    @Transactional
    override fun register(command: RegisterUserCommand): RegisterUserResult {
        val existingUser = userRepository.findByEmail(command.email)

        if (existingUser == null) {
            userValidator.register(
                email = command.email,
                nickname = command.nickname,
            )
        } else {
            userValidator.reactivate(
                user = existingUser,
                nickname = command.nickname,
            )
        }

        val passwordHash = requireNotNull(passwordEncoder.encode(command.password)) {
            "비밀번호 암호화 결과가 비어있습니다."
        }

        return if (existingUser == null) {
            registerNewUser(
                command = command,
                passwordHash = passwordHash,
            )
        } else {
            reactivateUser(
                user = existingUser,
                command = command,
                passwordHash = passwordHash,
            )
        }
    }

    private fun registerNewUser(
        command: RegisterUserCommand,
        passwordHash: String,
    ): RegisterUserResult {
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

    private fun reactivateUser(
        user: User,
        command: RegisterUserCommand,
        passwordHash: String,
    ): RegisterUserResult {
        val userId = requireNotNull(user.id)
        val savedUser = userRepository.save(user.reactivate(passwordHash))
        val userProfile = userProfileRepository.findByUserId(userId)
        val savedUserProfile = userProfileRepository.save(
            userProfile?.update(
                nickname = command.nickname,
                bio = command.bio,
                profileImageUrl = command.profileImageUrl,
            ) ?: UserProfile.register(
                userId = userId,
                nickname = command.nickname,
                bio = command.bio,
                profileImageUrl = command.profileImageUrl,
            )
        )

        return savedUser.toResult(savedUserProfile)
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

    @Transactional
    override fun withdraw(userId: Long) {
        val user = userRepository.findById(userId)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        val withdrawnUser = user.withdraw()
        userRepository.save(withdrawnUser)
        refreshTokenRepository.deleteByUserId(userId)
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
