package com.lyni.reader.lib.common.exceptions

import com.lyni.reader.lib.common.constants.Constants.CODE_UNKNOWN
import com.lyni.reader.lib.common.constants.Constants.UNKNOWN_ERROR

/**
 * @date 2022/3/5
 * @author Liangyong Ni
 * description 请求异常基类
 */
sealed class BaseException(
    private val code: Int,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    fun code() = code

    fun msg() = message!!

    override fun toString(): String {
        return "BaseException: $code $message"
    }

    class ServerException(code: Int = CODE_UNKNOWN, message: String = UNKNOWN_ERROR) : BaseException(code, message)

    class LocalException(code: Int = CODE_UNKNOWN, message: String = UNKNOWN_ERROR, cause: Throwable? = null) :
        BaseException(code, message, cause) {
        fun cause() = cause
    }
}