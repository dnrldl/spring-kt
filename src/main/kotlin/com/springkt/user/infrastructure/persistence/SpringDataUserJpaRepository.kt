package com.springkt.user.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataUserJpaRepository : JpaRepository<UserJpaEntity, Long> {
    fun findByEmail(email: String): UserJpaEntity?

    fun existsByEmail(email: String): Boolean
}
