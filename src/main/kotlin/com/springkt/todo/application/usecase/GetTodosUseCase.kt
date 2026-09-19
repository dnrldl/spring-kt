package com.springkt.todo.application.usecase

interface GetTodosUseCase {
    fun getTodos(userId: Long): List<TodoResult>
}
