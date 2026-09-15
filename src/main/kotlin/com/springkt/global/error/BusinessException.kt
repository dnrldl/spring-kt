package com.springkt.global.error

class BusinessException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.messageKey)
