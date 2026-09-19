package com.springkt.todo.presentation.dto

import com.springkt.todo.application.usecase.TodoResult

data class TodoResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val completed: Boolean,
) {
    companion object {
        fun from(result: TodoResult): TodoResponse = TodoResponse(
            id = result.id,
            title = result.title,
            description = result.description,
            completed = result.completed,
        )
    }
}
