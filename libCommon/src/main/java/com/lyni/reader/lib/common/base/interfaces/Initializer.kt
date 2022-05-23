package com.lyni.reader.lib.common.base.interfaces

import com.lyni.reader.lib.common.base.viewmodel.BaseViewModel

/**
 * @date 2022/5/10
 * @author Liangyong Ni
 * description Initializer
 */
interface Initializer {
    fun initData() {}

    fun initView()

    fun initViewModel(viewModel: BaseViewModel) {}

    fun initLiveData() {}

    fun initListener() {}
}