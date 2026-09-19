package com.springkt.todo.application.usecase

interface CreateTodoUseCase {
    fun createTodo(command: CreateTodoCommand): TodoResult
}
