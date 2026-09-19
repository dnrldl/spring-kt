package com.springkt.todo.application.usecase

interface DeleteTodoUseCase {
    fun deleteTodo(
        userId: Long,
        todoId: Long,
    )
}
