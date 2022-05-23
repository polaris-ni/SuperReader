package com.lyni.reader.lib.common.network.interceptor

import com.lyni.reader.lib.common.network.RetrofitManager
import com.lyni.treasure.lib.common.utils.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @date 2022/5/9
 * @author Liangyong Ni
 * description TokenInterceptor
 */
class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = RetrofitManager.networkInfo().token()
        if (token.isNotBlank()) {
            Log.d("RetrofitManager", "token: $token")
            val tokenRequest = request.newBuilder().header("token", token).build()
            return chain.proceed(tokenRequest)
        }
        return chain.proceed(request)
    }
}