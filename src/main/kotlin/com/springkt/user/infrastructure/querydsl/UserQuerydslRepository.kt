package com.springkt.user.infrastructure.querydsl

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.springkt.user.domain.model.User
import com.springkt.user.domain.query.UserProfileView
import com.springkt.user.domain.repository.UserQueryRepository
import com.springkt.user.infrastructure.persistence.QUserJpaEntity.userJpaEntity
import com.springkt.user.infrastructure.persistence.QUserProfileJpaEntity.userProfileJpaEntity
import org.springframework.stereotype.Repository

@Repository
class UserQuerydslRepository(
    private val queryFactory: JPAQueryFactory,
) : UserQueryRepository {

    override fun findById(id: Long): User? {
        return queryFactory
            .select(userJpaEntity)
            .from(userJpaEntity)
            .leftJoin(userProfileJpaEntity)
            .on(userProfileJpaEntity.userId.eq(userJpaEntity.id))
            .where(userJpaEntity.id.eq(id))
            .fetchOne()
            ?.toDomain()
    }

    override fun findProfileViewById(id: Long): UserProfileView? {
        return queryFactory
            .select(
                Projections.constructor(
                    UserProfileView::class.java,
                    userJpaEntity.id,
                    userJpaEntity.email,
                    userProfileJpaEntity.nickname,
                    userJpaEntity.role,
                    userJpaEntity.status,
                    userProfileJpaEntity.bio,
                    userProfileJpaEntity.profileImageUrl,
                ),
            )
            .from(userJpaEntity)
            .leftJoin(userProfileJpaEntity)
            .on(userProfileJpaEntity.userId.eq(userJpaEntity.id))
            .where(userJpaEntity.id.eq(id))
            .fetchOne()
    }
}
