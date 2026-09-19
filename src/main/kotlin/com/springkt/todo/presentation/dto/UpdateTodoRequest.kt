package com.springkt.todo.presentation.dto

import com.springkt.todo.application.usecase.UpdateTodoCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateTodoRequest(
    @field:NotBlank(message = "{validation.todo.title.required}")
    @field:Size(max = 200, message = "{validation.todo.title.size}")
    val title: String,

    @field:Size(max = 2000, message = "{validation.todo.description.size}")
    val description: String?,

    val completed: Boolean,
) {
    fun toCommand(
        userId: Long,
        todoId: Long,
    ): UpdateTodoCommand = UpdateTodoCommand(
        userId = userId,
        todoId = todoId,
        title = title,
        description = description,
        completed = completed,
    )
}
