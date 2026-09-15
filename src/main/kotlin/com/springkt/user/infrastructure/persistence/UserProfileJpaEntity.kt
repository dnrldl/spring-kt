package com.springkt.user.infrastructure.persistence

import com.springkt.global.audit.AuditableEntity
import com.springkt.user.domain.model.UserProfile
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "user_profiles")
class UserProfileJpaEntity(
    @Id
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,

    @Column(unique = true, length = 30)
    var nickname: String = "",

    @Column(length = 500)
    var bio: String? = null,

    @Column(length = 1000)
    var profileImageUrl: String? = null,
) : AuditableEntity() {
    fun toDomain(): UserProfile = UserProfile.reconstruct(
        userId = userId,
        nickname = nickname,
        bio = bio,
        profileImageUrl = profileImageUrl,
    )

    companion object {
        fun from(userProfile: UserProfile): UserProfileJpaEntity = UserProfileJpaEntity(
            userId = userProfile.userId,
            nickname = userProfile.nickname,
            bio = userProfile.bio,
            profileImageUrl = userProfile.profileImageUrl,
        )
    }
}
