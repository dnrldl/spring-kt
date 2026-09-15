package com.springkt.user.application.usecase

interface RegisterUserUseCase {
    fun register(command: RegisterUserCommand): RegisterUserResult
}
