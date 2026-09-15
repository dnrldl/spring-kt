package com.springkt.auth.application.usecase

interface LoginUseCase {
    fun login(command: LoginCommand): LoginResult
}
