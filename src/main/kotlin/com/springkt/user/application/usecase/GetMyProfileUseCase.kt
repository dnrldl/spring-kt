package com.springkt.user.application.usecase

interface GetMyProfileUseCase {
    fun getMyProfile(userId: Long): GetMyProfileResult
}
