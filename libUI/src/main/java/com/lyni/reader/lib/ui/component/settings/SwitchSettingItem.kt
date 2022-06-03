package com.lyni.reader.lib.ui.component.settings

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.databinding.adapters.ImageViewBindingAdapter.setImageDrawable
import com.lyni.reader.lib.common.ktx.layoutInflater
import com.lyni.reader.lib.ui.R
import com.lyni.reader.lib.ui.databinding.UiLayoutSwitchSettingItemBinding
import com.lyni.treasure.lib.common.ktx.*

/**
 * @date 2022/5/26
 * @author Liangyong Ni
 * description SwitchSettingItem
 */
class SwitchSettingItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var icon: Drawable? = null
    private var title: String? = null
    private var desc: String? = null
    private var checked: Boolean = false
    private var itemEnabled: Boolean = true

    private var onChange: ((Boolean) -> Unit)? = null

    private val binding: UiLayoutSwitchSettingItemBinding

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.SwitchSettingItem)
        icon = array.getResourceId(R.styleable.SwitchSettingItem_setting_icon, -1).run {
            try {
                getDrawable
            } catch (e: Exception) {
                null
            }
        }
        title = array.getString(R.styleable.SwitchSettingItem_setting_title)
        desc = array.getString(R.styleable.SwitchSettingItem_setting_desc)
        checked = array.getBoolean(R.styleable.SwitchSettingItem_checked, false)
        itemEnabled = array.getBoolean(R.styleable.SwitchSettingItem_checked, true)
        val isShow = array.getBoolean(R.styleable.SwitchSettingItem_show_switch, true)
        array.recycle()
        binding = UiLayoutSwitchSettingItemBinding.inflate(layoutInflater)
        isShow.positive { binding.sbMain.visible() }.otherwise { binding.sbMain.gone() }
        binding.tvTitle.text = title
        setDesc(desc)
        binding.sbMain.setChecked(checked)
        itemEnabled.negative { binding.sbMain.setEnable(false) }
        setIcon(icon)
        binding.sbMain.setOnSwitchListener {
            checked = it
            onChange?.invoke(checked)
        }
        addView(binding.root)
        isClickable = itemEnabled
    }

    fun setTitle(title: String?) {
        binding.tvTitle.text = title
    }

    fun setDesc(desc: String?) {
        if (desc.isNullOrBlank()) {
            binding.tvDesc.gone()
        } else {
            binding.tvDesc.visible()
            binding.tvDesc.text = desc
        }
    }

    fun enableSettingItem(isEnabled: Boolean) {
        itemEnabled = isEnabled
        binding.sbMain.setEnable(isEnabled)
    }

    fun setOnChangeListener(onChangeListener: ((Boolean) -> Unit)) {
        this.onChange = onChangeListener
    }

    fun setIcon(icon: Drawable?) {
        binding.ivIcon.run {
            icon?.let {
                visible()
                setImageDrawable(it)
            } ?: gone()
        }
    }

    fun getSwitchButton() = binding.sbMain
}