package com.springkt.todo.application.usecase

interface GetTodoUseCase {
    fun getTodo(
        userId: Long,
        todoId: Long,
    ): TodoResult
}
