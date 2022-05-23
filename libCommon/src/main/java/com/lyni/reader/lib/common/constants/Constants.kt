package com.lyni.reader.lib.common.constants

/**
 * @date 2022/3/5
 * @author Liangyong Ni
 * description 状态码
 */
object Constants {
    const val CODE_SUCCESS = 0
    const val CODE_UNKNOWN = 21000
    const val CODE_SERVER_ERROR = 21001
    const val CODE_JSON_PARSE_ERROR = 21002
    const val CODE_TIMEOUT = 21002

    const val CODE_TOKEN_INVALID = 20002

    const val ACTIVITY_NOT_ATTACH = 60001

    const val SERVER_ERROR = "Server Error"
    const val UNKNOWN_ERROR = "Server Error"
    const val JSON_PARSE_ERROR = "JSON解析错误"
    const val TIMEOUT = "网络连接超时"
}