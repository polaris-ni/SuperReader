package com.lyni.reader.lib.common.network.entity

import androidx.annotation.Keep

/**
 * @date 2022/3/5
 * @author Liangyong Ni
 * description 响应体
 */
@Keep
data class Response<T>(
    var code: Int = 0,
    var message: String? = "",
    var success: Boolean,
    val result: T
) {
    companion object {
        fun <T> fromCache(data: T): Response<T> =
            Response(message = "FROM CACHE", success = true, result = data)
    }
}