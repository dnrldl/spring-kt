package com.springkt.todo.application.usecase

import com.springkt.todo.domain.model.Todo

data class TodoResult(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String?,
    val completed: Boolean,
)

fun Todo.toResult(): TodoResult = TodoResult(
    id = requireNotNull(id),
    userId = userId,
    title = title,
    description = description,
    completed = completed,
)
