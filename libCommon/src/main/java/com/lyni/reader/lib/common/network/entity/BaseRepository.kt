package com.lyni.reader.lib.common.network.entity

import com.lyni.reader.lib.common.constants.Constants
import com.lyni.reader.lib.common.exceptions.BaseException
import com.lyni.reader.lib.common.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @date 2022/3/21
 * @author Liangyong Ni
 * description 数据源基类
 */
abstract class BaseRepository<S>(serviceClz: Class<S>) {
    val apiService: S = RetrofitManager.createService(serviceClz)

    @Throws(BaseException::class)
    suspend fun <T> net(call: suspend () -> Response<T>): Response<T> =
        withContext(Dispatchers.IO) {
            try {
                call.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is BaseException.ServerException && e.code() == Constants.CODE_TOKEN_INVALID) {
                    RetrofitManager.networkInfo().token("")
                    RetrofitManager.networkInfo().refresh()
                    net(call)
                } else {
                    if (e is BaseException) {
                        throw e
                    } else {
                        throw BaseException.LocalException(message = e.localizedMessage ?: Constants.UNKNOWN_ERROR)
                    }
                }
            }
        }
}