package com.lyni.reader.lib.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.lyni.reader.lib.common.R
import com.lyni.reader.lib.common.ktx.appCtx
import com.lyni.treasure.lib.common.ktx.getString
import com.lyni.treasure.lib.common.utils.ToastUtil

/**
 * @date 2022/5/25
 * @author Liangyong Ni
 * description IntentHelp
 */
@Suppress("unused")
object IntentHelp {

    fun getBrowserIntent(url: String): Intent {
        return getBrowserIntent(Uri.parse(url))
    }

    fun getBrowserIntent(uri: Uri): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (intent.resolveActivity(appCtx.packageManager) == null) {
            return Intent.createChooser(intent, "请选择浏览器")
        }
        return intent
    }

    fun openTTSSetting() {
        //跳转到文字转语音设置界面
        kotlin.runCatching {
            val intent = Intent()
            intent.action = "com.android.settings.TTS_SETTINGS"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            appCtx.startActivity(intent)
        }.onFailure {
            ToastUtil.showToast(R.string.tip_cannot_jump_setting_page.getString)
        }
    }

    fun toInstallUnknown(context: Context) {
        kotlin.runCatching {
            val intent = Intent()
            intent.action = "android.settings.MANAGE_UNKNOWN_APP_SOURCES"
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }.onFailure {
            ToastUtil.showToast("无法打开设置")
        }
    }

}