package com.springkt.auth.infrastructure.redis

import org.springframework.data.repository.CrudRepository

interface SpringDataRefreshTokenRedisRepository : CrudRepository<RefreshTokenRedisEntity, Long>
