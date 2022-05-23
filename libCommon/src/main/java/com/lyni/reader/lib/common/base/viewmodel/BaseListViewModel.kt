package com.lyni.reader.lib.common.base.viewmodel

import androidx.lifecycle.MutableLiveData
import com.lyni.reader.lib.common.base.component.safeLaunch
import com.lyni.treasure.lib.common.ktx.toLive

/**
 * @date 2022/5/9
 * @author Liangyong Ni
 * description BaseListViewModel
 */
abstract class BaseListViewModel<T : Any> : BaseViewModel() {
    // 当前页码
    protected var mPage = 0

    // 页面大小
    protected var mPageSize = 10

    // 加载的数据
    protected val mListData: MutableLiveData<MutableList<T>> = MutableLiveData()

    fun getListDataLive() = mListData.toLive()

    /**
     * 加载数据
     * 改进添加默认协程执行
     * @param mPage page index
     * @param mPageSize page size
     */
    abstract suspend fun loadData(mPage: Int, mPageSize: Int)

    /**
     * 加载数据
     */
    open fun loadMore() {
        mPage++
        safeLaunch {
            loadData(mPage, mPageSize)
        }
    }

    /**
     * 刷新数据
     */
    open fun refresh() {
        mPage = 0
        safeLaunch {
            loadData(mPage, mPageSize)
        }
    }

    /**
     * 是否是第一页
     * @return true表示第一页，反之不是
     */
    open fun isFirstPage(): Boolean = (mPage == 0)

    /**
     * 是否是最后一页
     * @return true表示最后一页，反之不是
     */
    open fun isLastPage() = mPageSize > (mListData.value?.size ?: 0)

    /**
     * 数据是否为空
     * @return true表示数据为空，反之不是
     */
    open fun isEmpty() = if (mPage == 0 && mListData.value.isNullOrEmpty()) {
        isEmpty.postValue(true)
        true
    } else {
        false
    }
}