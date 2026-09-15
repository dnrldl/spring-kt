package com.springkt.auth.presentation.dto

import com.springkt.auth.application.usecase.LoginCommand
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:Email(message = "{validation.email.invalid}")
    @field:NotBlank(message = "{validation.email.required}")
    val email: String,

    @field:NotBlank(message = "{validation.password.required}")
    val password: String,
) {
    fun toCommand(dpopKeyThumbprint: String): LoginCommand = LoginCommand(
        email = email,
        password = password,
        dpopKeyThumbprint = dpopKeyThumbprint,
    )
}
