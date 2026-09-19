package com.springkt.todo.presentation.dto

import com.springkt.todo.application.usecase.CreateTodoCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateTodoRequest(
    @field:NotBlank(message = "{validation.todo.title.required}")
    @field:Size(max = 200, message = "{validation.todo.title.size}")
    val title: String,

    @field:Size(max = 2000, message = "{validation.todo.description.size}")
    val description: String?,
) {
    fun toCommand(userId: Long): CreateTodoCommand = CreateTodoCommand(
        userId = userId,
        title = title,
        description = description,
    )
}
