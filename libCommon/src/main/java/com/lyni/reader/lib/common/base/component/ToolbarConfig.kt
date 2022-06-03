package com.lyni.reader.lib.common.base.component

import androidx.lifecycle.LifecycleObserver
import com.lyni.reader.lib.common.R
import com.lyni.reader.lib.common.databinding.CommonLayoutToolbarBinding
import com.lyni.treasure.lib.common.ktx.*

/**
 * @date 2022/3/11
 * @author Liangyong Ni
 * description toolbar config
 */
class ToolbarConfig(private val binding: CommonLayoutToolbarBinding) : LifecycleObserver {

    fun setBack(showBack: Boolean = true, backResId: Int = 0, listener: (() -> Unit)? = {}): ToolbarConfig {
        binding.apply {
            if (showBack) {
                ivReturn.visible()
                if (backResId > 0) {
                    ivReturn.setImageDrawable(backResId.getDrawable)
                }
                ivReturn.onClick { listener?.invoke() }
            } else {
                ivReturn.gone()
            }
        }
        return this
    }

    fun backgroundColor(backgroundColor: Int = R.color.white): ToolbarConfig {
        binding.toolbar.setBackgroundColor(backgroundColor.getColor)
        return this
    }

    fun title(text: String? = null, textColor: Int = -1): ToolbarConfig {
        binding.apply {
            text?.let {
                tvTitle.visible()
                tvTitle.text = text
                (textColor > 0).positive { tvTitle.setTextColor(textColor.getColor) }
            } ?: let {
                tvTitle.gone()
            }
        }
        return this
    }

    fun menu(isShow: Boolean = true, menuResId: Int? = R.drawable.common_ic_more_24, click: (() -> Unit)?): ToolbarConfig {
        if (!isShow) {
            binding.ivMenu.gone()
            return this
        }
        binding.apply {
            menuResId?.let {
                ivMenu.visible()
                ivMenu.setImageDrawable(menuResId.getDrawable)
            } ?: let {
                ivMenu.gone()
            }
        }
        binding.ivMenu.onClick {
            click?.invoke()
        }
        return this
    }
}