package com.springkt.todo.domain.repository

import com.springkt.todo.domain.model.Todo

interface TodoRepository {
    fun save(todo: Todo): Todo

    fun findByIdAndUserId(
        id: Long,
        userId: Long,
    ): Todo?

    fun findAllByUserId(userId: Long): List<Todo>

    fun deleteById(id: Long)
}
