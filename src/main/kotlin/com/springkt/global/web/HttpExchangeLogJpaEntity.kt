package com.springkt.global.web

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(
    name = "http_exchange_logs",
    indexes = [
        Index(name = "idx_http_exchange_logs_requested_at", columnList = "requested_at"),
        Index(name = "idx_http_exchange_logs_path_status", columnList = "path,status"),
    ],
)
class HttpExchangeLogJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "request_id", nullable = false, unique = true, length = 36)
    var requestId: String = "",

    @Column(nullable = false, length = 10)
    var method: String = "",

    @Column(nullable = false, length = 500)
    var path: String = "",

    @Column(name = "query_string", columnDefinition = "text")
    var queryString: String? = null,

    @Column(nullable = false)
    var status: Int = 0,

    @Column(name = "elapsed_ms", nullable = false)
    var elapsedMs: Long = 0,

    @Column(name = "client_ip", length = 45)
    var clientIp: String? = null,

    @Column(name = "user_agent", length = 500)
    var userAgent: String? = null,

    @Column(name = "request_headers", columnDefinition = "text")
    var requestHeaders: String? = null,

    @Column(name = "request_body", columnDefinition = "text")
    var requestBody: String? = null,

    @Column(name = "response_headers", columnDefinition = "text")
    var responseHeaders: String? = null,

    @Column(name = "response_body", columnDefinition = "text")
    var responseBody: String? = null,

    @Column(name = "requested_at", nullable = false)
    var requestedAt: Instant = Instant.now(),
)
