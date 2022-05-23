package com.lyni.reader.lib.common.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.lyni.reader.lib.common.network.entity.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

/**
 * @date 2022/5/9
 * @author Liangyong Ni
 * description BaseViewModel
 */
open class BaseViewModel : ViewModel() {

    private val isLoading = UnPeekLiveData<Boolean>()

    protected val isEmpty = UnPeekLiveData<Boolean>()

    private val error = UnPeekLiveData<Throwable?>()

    fun isLoadingLiveData(): LiveData<Boolean> = isLoading
    fun isEmptyLiveData(): LiveData<Boolean> = isEmpty
    fun errorLiveData(): LiveData<Throwable?> = error

    fun <T> Flow<T>.flowLoading(): Flow<T> {
        return this.onStart {
            isLoading.postValue(true)
            error.postValue(null)
            isEmpty.postValue(false)
        }.onCompletion {
            isLoading.postValue(false)
        }
    }

    protected fun <T> net(call: suspend () -> Response<T>) = flow {
        emit(call.invoke().result)
    }.onCompletion {
        it?.printStackTrace()
        error.postValue(it)
    }
}