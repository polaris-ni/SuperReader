package com.lyni.reader.lib.common.base.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * @date 2022/3/17
 * @author Liangyong Ni
 * description base view holder
 */
open class CustomBaseViewHolder(view: View) : BaseViewHolder(view) {

//    open fun loadImage(@IdRes viewId: Int, file: File?, placeholderId: Int = 0, errorId: Int = 0) =
//        this.apply {
//            Glide.with(itemView.context).load(file).placeholder(placeholderId).error(errorId)
//                .into(getView(viewId))
//        }

//    open fun loadImage(
//        @IdRes viewId: Int,
//        resourceId: Int?,
//        placeholderId: Int = 0,
//        errorId: Int = 0
//    ) = this.apply {
//        Glide.with(itemView.context).load(resourceId).placeholder(placeholderId).error(errorId)
//            .into(getView(viewId))
//    }

    open fun setOnClickListener(@IdRes viewId: Int, onClick: View.OnClickListener?) = this.apply {
        getView<View>(viewId).setOnClickListener(onClick)
    }

    fun getIV(@IdRes viewId: Int): ImageView = getView(viewId)

    fun getTV(@IdRes viewId: Int): TextView = getView(viewId)
}