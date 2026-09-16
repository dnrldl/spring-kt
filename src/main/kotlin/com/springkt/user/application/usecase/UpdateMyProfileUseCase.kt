package com.springkt.user.application.usecase

interface UpdateMyProfileUseCase {
    fun updateMyProfile(command: UpdateMyProfileCommand): UpdateMyProfileResult
}
