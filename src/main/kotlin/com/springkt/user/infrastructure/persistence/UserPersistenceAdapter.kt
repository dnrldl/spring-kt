package com.springkt.user.infrastructure.persistence

import com.springkt.user.domain.model.User
import com.springkt.user.domain.repository.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserPersistenceAdapter(
    private val springDataUserJpaRepository: SpringDataUserJpaRepository,
) : UserRepository {

    override fun save(user: User): User {
        return springDataUserJpaRepository.save(UserJpaEntity.from(user)).toDomain()
    }

    override fun findById(id: Long): User? {
        return springDataUserJpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByEmail(email: String): User? {
        return springDataUserJpaRepository.findByEmail(email.trim().lowercase())?.toDomain()
    }

    override fun existsByEmail(email: String): Boolean =
        springDataUserJpaRepository.existsByEmail(email.trim().lowercase())
}
