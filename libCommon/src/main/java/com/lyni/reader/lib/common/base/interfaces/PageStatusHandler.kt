package com.lyni.reader.lib.common.base.interfaces

/**
 * @date 2022/5/10
 * @author Liangyong Ni
 * description PageStatusHandler
 */
interface PageStatusHandler {

    companion object {
        const val STATUS_DEFAULT = 0
        const val STATUS_LOADING = 1
        const val STATUS_EMPTY = 2
        const val STATUS_ERROR = 3
    }

    fun showError()

    fun dismissError()

    fun showEmpty()

    fun dismissEmpty()

    fun showLoading()

    fun dismissLoading()

}