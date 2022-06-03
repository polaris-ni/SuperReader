package com.lyni.reader.lib.common.constants

import android.annotation.SuppressLint
import android.provider.Settings
import com.lyni.reader.lib.common.R
import com.lyni.reader.lib.common.ktx.appCtx
import com.lyni.treasure.lib.common.utils.Utils

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

    const val SERVER_ERROR = "Server Error"
    const val UNKNOWN_ERROR = "Server Error"
    const val JSON_PARSE_ERROR = "JSON解析错误"
    const val TIMEOUT = "网络连接超时"

    val androidId: String by lazy {
        Settings.System.getString(Utils.getAppContext().contentResolver, Settings.Secure.ANDROID_ID)
    }

    const val rootGroupId = -100L
    const val bookGroupAllId = -1L
    const val bookGroupLocalId = -2L
    const val bookGroupAudioId = -3L
    const val bookGroupNoneId = -4L

    const val notificationIdRead = -1122391
    const val notificationIdAudio = -1122392
    const val notificationIdCache = -1122393
    const val notificationIdWeb = -1122394
    const val notificationIdDownload = -1122395
    const val notificationIdCheckSource = -1122395

    const val UA_NAME = "User-Agent"

    /**
     * The authority of a FileProvider defined in a <provider> element in your app's manifest.
     */
    const val authority = "com.lyni.reader" + ".fileProvider"

    @SuppressLint("PrivateResource")
    val sysElevation = appCtx.resources.getDimension(R.dimen.design_appbar_elevation).toInt()


}