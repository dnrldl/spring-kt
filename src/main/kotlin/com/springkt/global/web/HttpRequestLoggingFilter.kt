package com.springkt.global.web

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant
import java.util.UUID

@Component
class HttpRequestLoggingFilter(
    private val httpExchangeLogJpaRepository: SpringDataHttpExchangeLogJpaRepository,
    private val httpExchangeLogSanitizer: HttpExchangeLogSanitizer,
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(HttpRequestLoggingFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val requestId = UUID.randomUUID().toString()
        val requestedAt = Instant.now()
        val startedAt = System.currentTimeMillis()
        val wrappedRequest = ContentCachingRequestWrapper(request, BODY_CACHE_LIMIT)
        val wrappedResponse = ContentCachingResponseWrapper(response)

        MDC.put(REQUEST_ID_KEY, requestId)
        wrappedResponse.setHeader(REQUEST_ID_HEADER, requestId)

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            try {
                val elapsedMs = System.currentTimeMillis() - startedAt
                log.info(
                    """
                    http_request
                    requestId={}
                    method={}
                    path={}
                    query={}
                    status={}
                    elapsedMs={}
                    clientIp={}
                    """.trimIndent(),
                    requestId,
                    request.method,
                    request.requestURI,
                    httpExchangeLogSanitizer.sanitizeQueryString(request.queryString) ?: "-",
                    wrappedResponse.status,
                    elapsedMs,
                    request.clientIp(),
                )
                saveHttpExchangeLog(
                    requestId = requestId,
                    request = wrappedRequest,
                    response = wrappedResponse,
                    requestedAt = requestedAt,
                    elapsedMs = elapsedMs,
                )
                wrappedResponse.copyBodyToResponse()
            } finally {
                MDC.remove(REQUEST_ID_KEY)
            }
        }
    }

    private fun HttpServletRequest.clientIp(): String {
        return getHeader("X-Forwarded-For")
            ?.substringBefore(",")
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: remoteAddr
    }

    private fun saveHttpExchangeLog(
        requestId: String,
        request: ContentCachingRequestWrapper,
        response: ContentCachingResponseWrapper,
        requestedAt: Instant,
        elapsedMs: Long,
    ) {
        runCatching {
            httpExchangeLogJpaRepository.save(
                HttpExchangeLogJpaEntity(
                    requestId = requestId,
                    method = request.method,
                    path = request.requestURI,
                    queryString = httpExchangeLogSanitizer.sanitizeQueryString(request.queryString),
                    status = response.status,
                    elapsedMs = elapsedMs,
                    clientIp = request.clientIp(),
                    userAgent = request.getHeader("User-Agent"),
                    requestHeaders = request.sanitizedHeaders(),
                    requestBody = httpExchangeLogSanitizer.sanitizeBody(
                        bytes = request.contentAsByteArray,
                        characterEncoding = request.characterEncoding,
                        contentType = request.contentType,
                    ),
                    responseHeaders = response.sanitizedHeaders(),
                    responseBody = httpExchangeLogSanitizer.sanitizeBody(
                        bytes = response.contentAsByteArray,
                        characterEncoding = response.characterEncoding,
                        contentType = response.contentType,
                    ),
                    requestedAt = requestedAt,
                ),
            )
        }.onFailure { exception ->
            log.warn("http_exchange_log_save_failed requestId={}", requestId, exception)
        }
    }

    private fun HttpServletRequest.sanitizedHeaders(): String {
        return httpExchangeLogSanitizer.sanitizeHeaders(
            headerNames = headerNames.asSequence().toList(),
            headerValue = { name -> getHeaders(name).asSequence().toList() },
        )
    }

    private fun HttpServletResponse.sanitizedHeaders(): String {
        return httpExchangeLogSanitizer.sanitizeHeaders(
            headerNames = headerNames,
            headerValue = { name -> getHeaders(name) },
        )
    }

    private companion object {
        const val REQUEST_ID_HEADER = "X-Request-Id"
        const val REQUEST_ID_KEY = "requestId"
        const val BODY_CACHE_LIMIT = Int.MAX_VALUE
    }
}
