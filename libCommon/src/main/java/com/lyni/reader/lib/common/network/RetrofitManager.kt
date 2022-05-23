package com.lyni.reader.lib.common.network

import com.lyni.reader.lib.common.network.interceptor.ResponseBodyInterceptor
import com.lyni.reader.lib.common.network.interceptor.TokenInterceptor
import com.lyni.treasure.lib.common.utils.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @date 2022/3/5
 * @author Liangyong Ni
 * description retrofit实例管理类
 */
/**
 * 网络访问流程
 *      1. Service接口返回 Response
 *      2. Repository层经由 httpCall 转换成 MallResponse
 *      3. ViewModel层使用 netEmit 发射数据或异常
 */
object RetrofitManager {
    private const val TAG: String = "RetrofitManager"

    private var instance: Retrofit? = null

    private lateinit var holder: NetworkHolder

    fun init(networkHolder: NetworkHolder) {
        holder = networkHolder
    }

    fun <T> createService(service: Class<T>): T {
        return genericRetrofit().create(service)
    }

    private fun genericRetrofit(): Retrofit {
        instance = instance ?: Retrofit.Builder()
            .baseUrl(holder.hostUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .client(genericOkClient())
            .build()
        return instance!!
    }

    private fun genericOkClient(): OkHttpClient {
        return genericHttpClientBuilder()
            .addInterceptor(TokenInterceptor())
            .addInterceptor(ResponseBodyInterceptor())
            .build()
    }

    private fun genericHttpClientBuilder(): OkHttpClient.Builder {
        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, message)
        }
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val builder = OkHttpClient.Builder()
            .connectTimeout(3_000L, TimeUnit.MILLISECONDS)
            .readTimeout(30_000, TimeUnit.MILLISECONDS)
            .writeTimeout(30_000, TimeUnit.MILLISECONDS)
        return builder.addInterceptor(httpLoggingInterceptor)
    }

    fun networkInfo() = holder
}