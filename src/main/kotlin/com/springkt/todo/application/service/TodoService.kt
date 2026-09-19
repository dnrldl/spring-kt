package com.springkt.todo.application.service

import com.springkt.global.error.BusinessException
import com.springkt.global.error.ErrorCode
import com.springkt.todo.application.usecase.CreateTodoCommand
import com.springkt.todo.application.usecase.CreateTodoUseCase
import com.springkt.todo.application.usecase.DeleteTodoUseCase
import com.springkt.todo.application.usecase.GetTodoUseCase
import com.springkt.todo.application.usecase.GetTodosUseCase
import com.springkt.todo.application.usecase.TodoResult
import com.springkt.todo.application.usecase.UpdateTodoCommand
import com.springkt.todo.application.usecase.UpdateTodoUseCase
import com.springkt.todo.application.usecase.toResult
import com.springkt.todo.domain.model.Todo
import com.springkt.todo.domain.repository.TodoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TodoService(
    private val todoRepository: TodoRepository,
) : CreateTodoUseCase,
    GetTodoUseCase,
    GetTodosUseCase,
    UpdateTodoUseCase,
    DeleteTodoUseCase {

    @Transactional
    override fun createTodo(command: CreateTodoCommand): TodoResult {
        val todo = Todo.create(
            userId = command.userId,
            title = command.title,
            description = command.description,
        )
        return todoRepository.save(todo).toResult()
    }

    @Transactional(readOnly = true)
    override fun getTodo(
        userId: Long,
        todoId: Long,
    ): TodoResult = findTodo(todoId, userId).toResult()

    @Transactional(readOnly = true)
    override fun getTodos(userId: Long): List<TodoResult> =
        todoRepository.findAllByUserId(userId).map(Todo::toResult)

    @Transactional
    override fun updateTodo(command: UpdateTodoCommand): TodoResult {
        val todo = findTodo(command.todoId, command.userId)
        val updatedTodo = todo.update(
            title = command.title,
            description = command.description,
            completed = command.completed,
        )
        return todoRepository.save(updatedTodo).toResult()
    }

    @Transactional
    override fun deleteTodo(
        userId: Long,
        todoId: Long,
    ) {
        findTodo(todoId, userId)
        todoRepository.deleteById(todoId)
    }

    private fun findTodo(
        todoId: Long,
        userId: Long,
    ): Todo = todoRepository.findByIdAndUserId(todoId, userId)
        ?: throw BusinessException(ErrorCode.TODO_NOT_FOUND)
}
