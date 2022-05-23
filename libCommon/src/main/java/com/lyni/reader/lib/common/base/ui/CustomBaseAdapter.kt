package com.lyni.reader.lib.common.base.ui

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.util.getItemView

/**
 * @date 2022/3/17
 * @author Liangyong Ni
 * description base adapter
 */
abstract class CustomBaseAdapter<T> @JvmOverloads constructor(
    @LayoutRes private val layoutResId: Int,
    data: MutableList<T>? = null
) : BaseQuickAdapter<T, CustomBaseViewHolder>(layoutResId, data) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): CustomBaseViewHolder {
        return CustomBaseViewHolder(parent.getItemView(layoutResId))
    }
}