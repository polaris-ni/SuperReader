package com.lyni.reader.lib.common.base.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.lyni.reader.lib.common.base.entity.DialogType
import com.lyni.reader.lib.common.R
import com.lyni.reader.lib.common.databinding.CommonDialogLoadingBinding
import com.lyni.reader.lib.common.exceptions.BaseException
import com.lyni.treasure.lib.common.ktx.getDrawable
import com.lyni.treasure.lib.common.ktx.gone
import com.lyni.treasure.lib.common.ktx.visible

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description 弹出的对话框：成功、失败、进行、默认
 */
class StatusDialog(context: Context) : Dialog(context) {

    private val binding by lazy {
        CommonDialogLoadingBinding.inflate(layoutInflater)
    }

    private val lottieAnimationView: LottieAnimationView by lazy {
        binding.lottieDialog
    }

    private val description: TextView by lazy {
        binding.textView
    }

    private val ivStatic: ImageView by lazy {
        binding.ivStatic
    }

    private val ivSuccess by lazy { R.drawable.common_ic_done_blue_24.getDrawable }
    private val ivWarning by lazy { R.drawable.common_ic_warning_24.getDrawable }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(R.drawable.common_bg_12_ffffff)
    }

    fun show(desc: String? = null, type: DialogType) {
        if ((desc?.length ?: 0) > 14) {
            throw BaseException.LocalException(message = "提示字符不能多于14个")
        }
        when (type) {
            DialogType.Default -> {
                lottieAnimationView.gone()
                ivStatic.gone()
            }
            DialogType.Executing -> {
                lottieAnimationView.visible()
                ivStatic.gone()
            }
            DialogType.Success -> {
                lottieAnimationView.gone()
                ivStatic.visible()
                ivStatic.setImageDrawable(ivSuccess)
            }
            DialogType.Warning -> {
                lottieAnimationView.gone()
                ivStatic.visible()
                ivStatic.setImageDrawable(ivWarning)
            }
        }
        description.text = desc
        show()
    }
}