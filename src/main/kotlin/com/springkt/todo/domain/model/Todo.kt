package com.springkt.todo.domain.model

data class Todo(
    val id: Long?,
    val userId: Long,
    val title: String,
    val description: String?,
    val completed: Boolean,
) {
    fun update(
        title: String,
        description: String?,
        completed: Boolean,
    ): Todo = copy(
        title = title.trim(),
        description = description?.trim()?.takeIf { it.isNotBlank() },
        completed = completed,
    )

    companion object {
        fun create(
            userId: Long,
            title: String,
            description: String?,
        ): Todo = Todo(
            id = null,
            userId = userId,
            title = title.trim(),
            description = description?.trim()?.takeIf { it.isNotBlank() },
            completed = false,
        )

        fun reconstruct(
            id: Long,
            userId: Long,
            title: String,
            description: String?,
            completed: Boolean,
        ): Todo = Todo(
            id = id,
            userId = userId,
            title = title,
            description = description,
            completed = completed,
        )
    }
}
