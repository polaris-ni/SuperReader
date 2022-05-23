package com.lyni.reader.lib.common.network

/**
 * @date 2022/5/9
 * @author Liangyong Ni
 * description NetworkHolder
 */
abstract class NetworkHolder {
    private var token = ""

    fun token() = token

    fun token(newToken: String) {
        token = newToken
    }

    abstract fun hostUrl(): String

    abstract suspend fun refresh(): String
}