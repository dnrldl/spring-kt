package com.springkt.user.domain.repository

import com.springkt.user.domain.model.User

interface UserRepository {
    fun save(user: User): User

    fun findById(id: Long): User?

    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean
}
