package com.springkt.global.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val messageKey: String,
) {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "error.invalid-request"),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "error.duplicated-email"),
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "error.duplicated-nickname"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "error.invalid-credentials"),
    DPOP_PROOF_REQUIRED(HttpStatus.UNAUTHORIZED, "error.dpop-proof-required"),
    INVALID_DPOP_PROOF(HttpStatus.UNAUTHORIZED, "error.invalid-dpop-proof"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "error.user-not-found"),
}
