package com.springkt.global.error

import org.springframework.context.MessageSource
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.Locale

@RestControllerAdvice
class GlobalExceptionHandler(
    private val messageSource: MessageSource,
) {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        exception: BusinessException,
        locale: Locale,
    ): ResponseEntity<ErrorResponse> {
        val errorCode = exception.errorCode
        return ResponseEntity
            .status(errorCode.status)
            .body(
                ErrorResponse(
                    code = errorCode.name,
                    message = getMessage(errorCode, locale),
                ),
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        exception: MethodArgumentNotValidException,
        locale: Locale,
    ): ResponseEntity<ErrorResponse> {
        val message = exception.bindingResult.fieldErrors.firstOrNull()?.defaultMessage
            ?: getMessage(ErrorCode.INVALID_REQUEST, locale)

        return ResponseEntity
            .status(ErrorCode.INVALID_REQUEST.status)
            .body(
                ErrorResponse(
                    code = ErrorCode.INVALID_REQUEST.name,
                    message = message,
                ),
            )
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(
        exception: DataIntegrityViolationException,
        locale: Locale,
    ): ResponseEntity<ErrorResponse> {
        val errorCode = if (exception.isEmailConstraintViolation()) {
            ErrorCode.DUPLICATED_EMAIL
        } else if (exception.isNicknameConstraintViolation()) {
            ErrorCode.DUPLICATED_NICKNAME
        } else {
            ErrorCode.INVALID_REQUEST
        }
        return ResponseEntity
            .status(errorCode.status)
            .body(
                ErrorResponse(
                    code = errorCode.name,
                    message = getMessage(errorCode, locale),
                ),
            )
    }

    private fun DataIntegrityViolationException.isEmailConstraintViolation(): Boolean {
        val message = mostSpecificCause.message.orEmpty().lowercase()
        return "email" in message
    }

    private fun DataIntegrityViolationException.isNicknameConstraintViolation(): Boolean {
        val message = mostSpecificCause.message.orEmpty().lowercase()
        return "nickname" in message
    }

    private fun getMessage(
        errorCode: ErrorCode,
        locale: Locale,
    ): String {
        return messageSource.getMessage(errorCode.messageKey, null, locale)
    }
}
