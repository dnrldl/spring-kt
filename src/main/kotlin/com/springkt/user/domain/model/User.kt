package com.springkt.user.domain.model

data class User(
    val id: Long?,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
    val status: UserStatus,
    val profile: UserProfile?
) {
    fun isActiveStatus(): Boolean = status == UserStatus.ACTIVE

    fun matchesPassword(
        matches: (rawPassword: String, encodedPassword: String) -> Boolean,
        rawPassword: String
    ): Boolean =
        isActiveStatus() && matches(rawPassword, passwordHash)

    companion object {
        fun register(
            email: String,
            passwordHash: String,
        ): User = User(
            id = null,
            email = email.trim().lowercase(),
            passwordHash = passwordHash,
            role = UserRole.USER,
            status = UserStatus.ACTIVE,
            profile = null
        )

        fun reconstruct(
            id: Long?,
            email: String,
            passwordHash: String,
            role: UserRole,
            status: UserStatus,
            profile: UserProfile? = null
        ): User = User(
            id = id,
            email = email,
            passwordHash = passwordHash,
            role = role,
            status = status,
            profile = profile
        )
    }
}
