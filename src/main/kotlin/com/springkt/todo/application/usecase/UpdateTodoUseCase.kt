package com.springkt.todo.application.usecase

interface UpdateTodoUseCase {
    fun updateTodo(command: UpdateTodoCommand): TodoResult
}
