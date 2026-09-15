package com.springkt.user.domain.repository

import com.springkt.user.domain.model.User
import com.springkt.user.domain.query.UserProfileView

interface UserQueryRepository {
    fun findById(id: Long): User?

    fun findProfileViewById(id: Long): UserProfileView?
}
