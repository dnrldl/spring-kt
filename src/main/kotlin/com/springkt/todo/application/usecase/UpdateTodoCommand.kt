package com.springkt.todo.application.usecase

data class UpdateTodoCommand(
    val userId: Long,
    val todoId: Long,
    val title: String,
    val description: String?,
    val completed: Boolean,
)
