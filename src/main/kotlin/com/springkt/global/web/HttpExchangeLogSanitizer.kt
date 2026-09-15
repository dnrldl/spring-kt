package com.springkt.global.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import java.nio.charset.Charset

@Component
class HttpExchangeLogSanitizer(
    private val objectMapper: ObjectMapper,
) {

    fun sanitizeQueryString(queryString: String?): String? {
        return queryString
            ?.takeIf { it.isNotBlank() }
            ?.split("&")
            ?.joinToString("&") { parameter ->
                val key = parameter.substringBefore("=", missingDelimiterValue = parameter)
                val value = parameter.substringAfter("=", missingDelimiterValue = "")

                if (key.isSensitiveKey(maskAuthorizationCode = true) && value.isNotBlank()) {
                    "$key=***"
                } else {
                    parameter
                }
            }
    }

    fun sanitizeHeaders(
        headerNames: Collection<String>,
        headerValue: (String) -> Collection<String>,
    ): String {
        val headers = headerNames.associateWith { name ->
            if (name.isSensitiveKey(maskAuthorizationCode = false)) {
                listOf("***")
            } else {
                headerValue(name).toList()
            }
        }
        return objectMapper.writeValueAsString(headers)
    }

    fun sanitizeBody(
        bytes: ByteArray,
        characterEncoding: String?,
        contentType: String?,
    ): String? {
        if (bytes.isEmpty()) {
            return null
        }
        if (!contentType.isTextContent()) {
            return "[binary content omitted]"
        }

        val body = bytes.toString(contentType.resolveCharset(characterEncoding))
        if (contentType.isFormContent()) {
            return sanitizeQueryString(body)
        }
        if (!contentType.isJsonContent()) {
            return body
        }

        return runCatching {
            val node = objectMapper.readTree(body)
            objectMapper.writeValueAsString(node.maskSensitiveValues())
        }.getOrElse { body }
    }

    private fun JsonNode.maskSensitiveValues(): JsonNode {
        return when (this) {
            is ObjectNode -> {
                properties().forEach { (fieldName, value) ->
                    if (fieldName.isSensitiveKey(maskAuthorizationCode = false)) {
                        put(fieldName, "***")
                    } else {
                        set<JsonNode>(fieldName, value.maskSensitiveValues())
                    }
                }
                this
            }

            is ArrayNode -> {
                for (index in 0 until size()) {
                    set(index, get(index).maskSensitiveValues())
                }
                this
            }

            else -> this
        }
    }

    private fun String?.isTextContent(): Boolean {
        if (this == null) {
            return false
        }
        val mediaType = runCatching { MediaType.parseMediaType(this) }.getOrNull()
        return mediaType?.type == "text" ||
            mediaType?.subtype?.contains("json") == true ||
            mediaType?.subtype?.contains("xml") == true ||
            mediaType?.subtype == "x-www-form-urlencoded"
    }

    private fun String?.isJsonContent(): Boolean {
        if (this == null) {
            return false
        }
        val mediaType = runCatching { MediaType.parseMediaType(this) }.getOrNull()
        return mediaType?.subtype?.contains("json") == true
    }

    private fun String?.isFormContent(): Boolean {
        if (this == null) {
            return false
        }
        val mediaType = runCatching { MediaType.parseMediaType(this) }.getOrNull()
        return mediaType?.subtype == "x-www-form-urlencoded"
    }

    private fun String?.resolveCharset(characterEncoding: String?): Charset {
        val mediaType = this?.let { runCatching { MediaType.parseMediaType(it) }.getOrNull() }
        val mediaTypeCharset = mediaType?.charset
        if (mediaTypeCharset != null) {
            return mediaTypeCharset
        }
        if (isJsonContent()) {
            return Charsets.UTF_8
        }
        return charset(characterEncoding ?: Charsets.UTF_8.name())
    }

    private fun String.isSensitiveKey(maskAuthorizationCode: Boolean): Boolean {
        val normalizedKey = lowercase()
            .replace("_", "")
            .replace("-", "")

        if (normalizedKey == "code") {
            return maskAuthorizationCode
        }

        return normalizedKey in SENSITIVE_KEYS ||
            "password" in normalizedKey ||
            "token" in normalizedKey ||
            "secret" in normalizedKey
    }

    private companion object {
        val SENSITIVE_KEYS = setOf(
            "accesstoken",
            "authorization",
            "cookie",
            "dpop",
            "refreshtoken",
            "setcookie",
        )
    }
}
