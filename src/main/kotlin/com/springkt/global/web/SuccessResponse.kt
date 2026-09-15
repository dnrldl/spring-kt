package com.springkt.global.web

data class SuccessResponse<T>(
    val success: Boolean,
    val data: T,
) {
    companion object {
        fun <T> ok(data: T): SuccessResponse<T> = SuccessResponse(
            success = true,
            data = data,
        )
    }
}
