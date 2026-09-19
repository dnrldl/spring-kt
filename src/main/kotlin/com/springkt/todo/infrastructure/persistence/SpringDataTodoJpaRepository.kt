package com.springkt.todo.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataTodoJpaRepository : JpaRepository<TodoJpaEntity, Long> {
    fun findByIdAndUserId(
        id: Long,
        userId: Long,
    ): TodoJpaEntity?

    fun findAllByUserIdOrderByIdDesc(userId: Long): List<TodoJpaEntity>
}
