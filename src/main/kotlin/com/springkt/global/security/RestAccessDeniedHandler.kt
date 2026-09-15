package com.springkt.global.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class RestAccessDeniedHandler : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        response.writer.write("""{"code":"FORBIDDEN","message":"접근 권한이 없습니다."}""")
    }
}
