package com.lyni.reader.lib.common.base.component

import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.lyni.reader.lib.common.base.ui.BaseActivity
import com.lyni.reader.lib.common.base.ui.BaseFragment
import com.lyni.reader.lib.common.base.viewmodel.BaseViewModel
import com.lyni.treasure.lib.common.ktx.safeLaunch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

/**
 * @date 2022/5/10
 * @author Liangyong Ni
 * description Ktx
 */
fun BaseActivity<*, *>.safeLaunch(task: suspend () -> Unit = {}) = lifecycleScope.safeLaunch(task)

fun BaseFragment<*, *>.safeLaunch(task: suspend () -> Unit = {}) = lifecycleScope.safeLaunch(task)

fun BaseViewModel.safeLaunch(task: suspend () -> Unit = {}) = viewModelScope.safeLaunch(task)

fun <T> Flow<T>.transmitCatch(onError: (e: Throwable) -> Unit = {}): Flow<T> {
    return this.catch {
        onError.invoke(it)
        throw it
    }
}