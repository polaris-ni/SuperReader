package com.lyni.reader.lib.common.network.interceptor

import com.lyni.reader.lib.common.constants.Constants
import com.lyni.reader.lib.common.exceptions.BaseException
import com.lyni.treasure.lib.common.utils.Log
import okhttp3.Interceptor
import okhttp3.Response
import okio.GzipSource
import org.json.JSONException
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.nio.charset.StandardCharsets


/**
 * @date 2022/3/5
 * @author Liangyong Ni
 * description 响应体拦截器
 */
class ResponseBodyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        try {
            val url = request.url.toString()
            val response = chain.proceed(request)
            if (!response.isSuccessful) {
                // 访问不成功
                Log.d("RetrofitManager", "<-- Failed At: $url")
                Log.d("RetrofitManager", "    Http Status Code: ${response.code}")
                Log.d("RetrofitManager", "    Response Body: ${response.body.string()}")
                throw BaseException.ServerException(Constants.CODE_SERVER_ERROR, Constants.SERVER_ERROR)
            }
            response.body.let {
                it.source().request(Long.MAX_VALUE)
                var buffer = it.source().buffer
                if ("gzip" == response.headers["Content-Encoding"]) {
                    buffer = okio.Buffer()
                    buffer.writeAll(GzipSource(buffer.clone()))
                }
                val contentType = it.contentType()
                val charset = if (contentType?.charset(StandardCharsets.UTF_8) == null) {
                    StandardCharsets.UTF_8
                } else {
                    contentType.charset(StandardCharsets.UTF_8)
                }
                if (charset != null && it.contentLength() != 0L) {
                    return parse(response, buffer.clone().readString(charset))
                }
            }
            return response
        } catch (timeoutException: SocketTimeoutException) {
            Log.d("RetrofitManager", timeoutException.message ?: "SocketTimeout")
            throw BaseException.LocalException(Constants.CODE_TIMEOUT, Constants.TIMEOUT, timeoutException)
        } catch (e: BaseException) {
            throw e
        } catch (e: Exception) {
            throw BaseException.LocalException(cause = e)
        }
    }

    private fun parse(response: Response, body: String): Response {
        val jsonObject: JSONObject?
        try {
            jsonObject = JSONObject(body)
        } catch (e: JSONException) {
            throw BaseException.LocalException(Constants.CODE_JSON_PARSE_ERROR, Constants.JSON_PARSE_ERROR, e)
        }
        val code = jsonObject.optInt("code")
        val success = jsonObject.optBoolean("success")
        val message = jsonObject.optString("message")
        if (code != Constants.CODE_SUCCESS || !success) {
            throw BaseException.ServerException(code, message)
        }
        return response
    }
}