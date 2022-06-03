package com.lyni.reader.lib.common.base.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.lyni.reader.lib.common.base.component.safeLaunch
import com.lyni.reader.lib.common.base.entity.CustomHolder
import com.lyni.reader.lib.common.base.entity.DialogType
import com.lyni.reader.lib.common.base.interfaces.Initializer
import com.lyni.reader.lib.common.base.interfaces.PageStatusHandler
import com.lyni.reader.lib.common.base.interfaces.PageStatusHandler.Companion.STATUS_DEFAULT
import com.lyni.reader.lib.common.base.interfaces.PageStatusHandler.Companion.STATUS_EMPTY
import com.lyni.reader.lib.common.base.interfaces.PageStatusHandler.Companion.STATUS_ERROR
import com.lyni.reader.lib.common.base.interfaces.PageStatusHandler.Companion.STATUS_LOADING
import com.lyni.reader.lib.common.base.viewmodel.BaseViewModel
import com.lyni.reader.lib.common.R
import com.lyni.reader.lib.common.databinding.CommonFragmentRootBinding
import com.lyni.reader.lib.common.databinding.CommonLayoutCustomHolderBinding
import com.lyni.reader.lib.common.exceptions.BaseException
import com.lyni.treasure.lib.common.ktx.*
import kotlinx.coroutines.delay
import java.lang.reflect.ParameterizedType

/**
 * @date 2022/3/5
 * @author Liangyong Ni
 * description Fragment基类
 */
abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment(), Initializer, PageStatusHandler {

    private lateinit var rootBinding: CommonFragmentRootBinding
    protected lateinit var binding: VB
    protected lateinit var viewModel: VM
    private var currentStatus: Int = STATUS_DEFAULT
    private val type = javaClass.genericSuperclass
    private val dialog: StatusDialog by lazy { StatusDialog(requireContext()) }
    private val customHolderView: CommonLayoutCustomHolderBinding by lazy { CommonLayoutCustomHolderBinding.inflate(layoutInflater) }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootBinding = CommonFragmentRootBinding.inflate(inflater)
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[0] as Class<VB>
            val method = clazz.getMethod("inflate", LayoutInflater::class.java)
            binding = method.invoke(null, layoutInflater) as VB
            viewModel = ViewModelProvider(this)[type.actualTypeArguments[1] as Class<VM>]
        }
        rootBinding.flMain.addView(binding.root, 0)
        return rootBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initViewModel(viewModel)
        initView()
        initLiveData()
        initListener()
    }

    /**
     * 初始化view
     */
    override fun initView() {
        rootBinding.refreshHeader.setAssetsFileName("lottie_refresh.json")
        rootBinding.refreshFooter.setAssetsFileName("lottie_refresh.json")
    }

    /**
     * 初始化LiveData
     */
    override fun initLiveData() {
        viewModel.isLoadingLiveData().observe(viewLifecycleOwner) {
            it.positive { showLoading() }.otherwise { dismissLoading() }
        }
        viewModel.isEmptyLiveData().observe(viewLifecycleOwner) {
            it.positive { showEmpty() }.otherwise { dismissEmpty() }
        }
        viewModel.errorLiveData().observe(viewLifecycleOwner) {
            if (it is BaseException) {
                showWarning(it.msg())
            } else {
                it?.let { showError() } ?: dismissError()
            }
        }
    }

    /**
     * 初始化监听器
     */
    override fun initListener() {
        getRefreshLayout().apply {
            setEnableRefresh(enableRefresh())
            setEnableLoadMore(enableLoadMore())
            setOnRefreshListener {
                refresh()
            }
        }
    }

    /**
     * 显示页面错误态
     */
    override fun showError() {
        if (currentStatus != STATUS_ERROR) {
            currentStatus = STATUS_ERROR
            setCustomHolderView(errorHolder())
            replaceMainView(customHolderView.root)
        }
    }

    /**
     * 恢复默认显示
     */
    override fun dismissError() {
        if (currentStatus == STATUS_ERROR) {
            currentStatus = STATUS_DEFAULT
            replaceMainView(binding.root)
        }
    }

    /**
     * 显示页面空态
     */
    override fun showEmpty() {
        if (currentStatus != STATUS_EMPTY) {
            currentStatus = STATUS_EMPTY
            setCustomHolderView(emptyHolder())
            replaceMainView(customHolderView.root)
        }
    }

    /**
     * 恢复默认显示
     */
    override fun dismissEmpty() {
        if (currentStatus == STATUS_EMPTY) {
            currentStatus = STATUS_DEFAULT
            replaceMainView(binding.root)
        }
    }

    /**
     * 显示加载框
     */
    override fun showLoading() {
        if (currentStatus != STATUS_LOADING) {
            currentStatus = STATUS_LOADING
            dialog.show(R.string.loading.getString, DialogType.Executing)
        }
    }

    /**
     * 关闭加载框
     */
    override fun dismissLoading() {
        safeLaunch {
            delay(300)
            currentStatus = STATUS_DEFAULT
            dialog.dismiss()
            getRefreshLayout().isRefreshing.positive {
                getRefreshLayout().finishRefresh()
            }
        }
    }

    /**
     * 下拉刷新功能是否启用
     */
    open fun enableRefresh(): Boolean = false


    /**
     * 上拉加载功能是否启动
     */
    open fun enableLoadMore(): Boolean = false

    /**
     * 页面刷新
     */
    open fun refresh() {
        currentStatus = STATUS_DEFAULT
        replaceMainView(binding.root)
    }

    /**
     * 替换main FrameLayout中的内容
     * @param view View
     */
    protected fun replaceMainView(view: View) {
        rootBinding.flMain.removeAllViews()
        rootBinding.flMain.addView(view, 0)
        getRefreshLayout().finishRefresh()
    }

    /**
     * 提供刷新视图的引用
     */
    fun getRefreshLayout() = rootBinding.srlRoot

    /**
     * 空态页面CustomHolder
     * @return CustomHolder
     */
    open fun emptyHolder() = CustomHolder(
        R.drawable.common_ic_empty_96,
        R.string.default_empty_title.getString,
        R.string.default_empty_desc.getString,
        true,
        R.string.refresh.getString
    ) {
        refresh()
    }

    /**
     * 错误态页面CustomHolder
     * @return CustomHolder
     */
    open fun errorHolder() = CustomHolder(
        R.drawable.common_ic_error_96,
        R.string.default_error_title.getString,
        R.string.default_error_desc.getString,
        true,
        R.string.refresh.getString
    ) {
        refresh()
    }

    /**
     * 设置CustomHolderView
     * @param holder CustomHolder
     */
    private fun setCustomHolderView(holder: CustomHolder) {
        customHolderView.apply {
            holder.title?.let { tvTitle.text = it } ?: tvTitle.gone()
            holder.description?.let { tvDesc.text = it } ?: tvDesc.gone()
            holder.imageRes?.getDrawable?.let {
                ivPlaceHolder.setImageDrawable(it)
            }
            holder.showButton.positive {
                btnAction.visible()
                btnAction.text = holder.btnText
                btnAction.onClick { holder.event.invoke() }
            }.otherwise {
                btnAction.gone()
            }
        }
    }

    /**
     * 跳转其他activity
     * @param target 目标
     * @param data 数据
     * @param finish 是否结束当前activity
     */
    fun navigateTo(target: String, data: Bundle? = null, finish: Boolean = false) {
        ARouter.getInstance().build(target)
            .with(data)
            .navigation(requireContext())
        finish.positive { requireActivity().finish() }
    }

    inline fun <reified T : Activity> navigateTo(isFinish: Boolean = false) {
        startActivity(Intent(requireContext(), T::class.java))
        isFinish.positive {
            requireActivity().finish()
        }
    }

    protected fun showDialog(desc: String, type: DialogType) {
        dialog.show(desc, type)
    }

    protected fun showWarning(desc: String, duration: Long = 2000L) {
        safeLaunch {
            dialog.show(desc, DialogType.Warning)
            delay(duration)
            dialogDismiss()
        }
    }

    protected fun showDefault(desc: String, duration: Long = 2000L) {
        safeLaunch {
            dialog.show(desc, DialogType.Default)
            delay(duration)
            dialogDismiss()
        }
    }

    protected fun showSuccess(desc: String, duration: Long = 2000L) {
        safeLaunch {
            dialog.show(desc, DialogType.Success)
            delay(duration)
            dialogDismiss()
        }
    }

    private fun dialogDismiss() {
        dialog.dismiss()
    }

    fun setRefreshHeaderBackground(@ColorInt color: Int? = null, @DrawableRes res: Int? = null) {
        color?.let { rootBinding.refreshHeader.setBackgroundColor(it) }
        res?.let { rootBinding.refreshHeader.setBackgroundResource(res) }
    }

    fun setRefreshFooterBackground(@ColorInt color: Int? = null, @DrawableRes res: Int? = null) {
        color?.let { rootBinding.refreshFooter.setBackgroundColor(it) }
        res?.let { rootBinding.refreshFooter.setBackgroundResource(res) }
    }

    fun setRootBackground(@ColorInt color: Int? = null, @DrawableRes res: Int? = null) {
        color?.let { rootBinding.srlRoot.setBackgroundColor(it) }
        res?.let { rootBinding.srlRoot.setBackgroundResource(res) }
    }
}