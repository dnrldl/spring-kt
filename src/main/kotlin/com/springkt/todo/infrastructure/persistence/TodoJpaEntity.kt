package com.springkt.todo.infrastructure.persistence

import com.springkt.global.audit.AuditableEntity
import com.springkt.todo.domain.model.Todo
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "todos")
class TodoJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var userId: Long = 0,

    @Column(nullable = false, length = 200)
    var title: String = "",

    @Column(length = 2000)
    var description: String? = null,

    @Column(nullable = false)
    var completed: Boolean = false,
) : AuditableEntity() {
    fun toDomain(): Todo = Todo.reconstruct(
        id = requireNotNull(id),
        userId = userId,
        title = title,
        description = description,
        completed = completed,
    )

    companion object {
        fun from(todo: Todo): TodoJpaEntity = TodoJpaEntity(
            id = todo.id,
            userId = todo.userId,
            title = todo.title,
            description = todo.description,
            completed = todo.completed,
        )
    }
}
