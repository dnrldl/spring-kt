package com.springkt.todo.infrastructure.persistence

import com.springkt.todo.domain.model.Todo
import com.springkt.todo.domain.repository.TodoRepository
import org.springframework.stereotype.Repository

@Repository
class TodoPersistenceAdapter(
    private val springDataTodoJpaRepository: SpringDataTodoJpaRepository,
) : TodoRepository {
    override fun save(todo: Todo): Todo =
        springDataTodoJpaRepository.save(TodoJpaEntity.from(todo)).toDomain()

    override fun findByIdAndUserId(
        id: Long,
        userId: Long,
    ): Todo? = springDataTodoJpaRepository.findByIdAndUserId(id, userId)?.toDomain()

    override fun findAllByUserId(userId: Long): List<Todo> =
        springDataTodoJpaRepository.findAllByUserIdOrderByIdDesc(userId).map(TodoJpaEntity::toDomain)

    override fun deleteById(id: Long) {
        springDataTodoJpaRepository.deleteById(id)
    }
}
