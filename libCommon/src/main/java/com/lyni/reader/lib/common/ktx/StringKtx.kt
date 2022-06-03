package com.lyni.reader.lib.common.ktx

import android.icu.text.Collator
import android.icu.util.ULocale
import android.net.Uri
import java.io.File
import java.util.*

/**
 * @date 2022/5/25
 * @author Liangyong Ni
 * description StringKtx
 */
fun String?.isAbsUrl() =
    this?.let {
        it.startsWith("http://", true) || it.startsWith("https://", true)
    } ?: false

fun String?.safeTrim() = if (this.isNullOrBlank()) null else this.trim()

fun String?.isContentScheme(): Boolean = this?.startsWith("content://") == true

fun String.parseToUri(): Uri {
    return if (isContentScheme()) {
        Uri.parse(this)
    } else {
        Uri.fromFile(File(this))
    }
}

fun String.cnCompare(other: String): Int {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Collator.getInstance(ULocale.SIMPLIFIED_CHINESE).compare(this, other)
    } else {
        java.text.Collator.getInstance(Locale.CHINA).compare(this, other)
    }
}

/**
 * 将字符串拆分为单个字符,包含emoji
 */
fun String.toStringArray(): Array<String> {
    var codePointIndex = 0
    return try {
        Array(codePointCount(0, length)) {
            val start = codePointIndex
            codePointIndex = offsetByCodePoints(start, 1)
            substring(start, codePointIndex)
        }
    } catch (e: Exception) {
        split("").toTypedArray()
    }
}