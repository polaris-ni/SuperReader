package com.lyni.reader

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.lyni.treasure.lib.common.utils.Log

/**
 * @date 2022/5/24
 * @author Liangyong Ni
 * description App
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            ARouter.openLog() // 打印日志
            ARouter.openDebug() // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            Log.openDebug()
        }
        ARouter.init(this) // 尽可能早，推荐在Application中初始化
//        MMKVUtil.init(this) // MMKV初始化
        // 网络初始化
//        RetrofitManager.init(object : NetworkHolder() {
//            private val username by lazy { MMKVUtil.decodeString(MMKVKey.USERNAME) ?: "" }
//            private val password by lazy { MMKVUtil.decodeString(MMKVKey.PASSWORD) ?: "" }
//
//            override fun hostUrl(): String = "http://124.220.174.71:1029"
//
//            override suspend fun refresh(): String {
//                val response = AccountRepository.instance.token(username, password)
//                token(response.result)
//                return token()
//            }
//        })
//        // bugly初始化
//        CrashReport.initCrashReport(applicationContext, "9eea32843f", false)
    }
}