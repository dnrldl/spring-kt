package com.springkt.global.audit

import com.springkt.global.security.JwtPrincipal
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

object AuditContext {

    fun currentActor(): String {
        val principal = SecurityContextHolder.getContext().authentication?.principal

        return when (principal) {
            is JwtPrincipal -> principal.userId.toString()
            is String -> principal.takeIf { it.isNotBlank() && it != "anonymousUser" } ?: SYSTEM_ACTOR
            else -> SYSTEM_ACTOR
        }
    }

    fun currentIp(): String? {
        val request = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)
            ?.request
            ?: return null

        return request.clientIp()
    }

    private fun HttpServletRequest.clientIp(): String? {
        return getHeader("X-Forwarded-For")
            ?.substringBefore(",")
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: getHeader("X-Real-IP")
                ?.trim()
                ?.takeIf { it.isNotBlank() }
            ?: remoteAddr
    }

    private const val SYSTEM_ACTOR = "system"
}
