package com.lyni.reader.lib.common.utils

import android.util.Log
import com.lyni.reader.lib.common.BuildConfig

object AppLog {

    private val mLogs = arrayListOf<Triple<Long, String, Throwable?>>()

    val logs get() = mLogs.toList()

    @Synchronized
    fun put(message: String?, throwable: Throwable? = null) {
        message ?: return
        if (mLogs.size > 100) {
            mLogs.removeLastOrNull()
        }
        mLogs.add(0, Triple(System.currentTimeMillis(), message, throwable))
        if (throwable != null) {
            if (BuildConfig.DEBUG) {
                val stackTrace = Thread.currentThread().stackTrace
                Log.e(stackTrace[3].className, message, throwable)
            }
        }
    }

    @Synchronized
    fun clear() {
        mLogs.clear()
    }

    fun putDebug(message: String?, throwable: Throwable? = null) {
//        if (AppConfig.recordLog || BuildConfig.DEBUG) {
        if (BuildConfig.DEBUG) {
            put(message, throwable)
        }
    }
}