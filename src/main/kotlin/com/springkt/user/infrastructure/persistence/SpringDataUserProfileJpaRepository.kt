package com.springkt.user.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataUserProfileJpaRepository : JpaRepository<UserProfileJpaEntity, Long> {
    fun existsByNickname(nickname: String): Boolean
}
