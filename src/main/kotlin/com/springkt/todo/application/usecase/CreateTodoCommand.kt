package com.springkt.todo.application.usecase

data class CreateTodoCommand(
    val userId: Long,
    val title: String,
    val description: String?,
)
