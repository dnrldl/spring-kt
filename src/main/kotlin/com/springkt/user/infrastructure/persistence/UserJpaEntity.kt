package com.springkt.user.infrastructure.persistence

import com.springkt.global.audit.AuditableEntity
import com.springkt.user.domain.model.User
import com.springkt.user.domain.model.UserRole
import com.springkt.user.domain.model.UserStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var email: String = "",

    @Column(nullable = false)
    var passwordHash: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.USER,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: UserStatus = UserStatus.ACTIVE,
) : AuditableEntity() {
    fun toDomain(): User = User.reconstruct(
        id = id,
        email = email,
        passwordHash = passwordHash,
        role = role,
        status = status,
    )

    companion object {
        fun from(user: User): UserJpaEntity = UserJpaEntity(
            id = user.id,
            email = user.email,
            passwordHash = user.passwordHash,
            role = user.role,
            status = user.status,
        )
    }
}
