package com.springkt.global.web

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataHttpExchangeLogJpaRepository : JpaRepository<HttpExchangeLogJpaEntity, Long>
