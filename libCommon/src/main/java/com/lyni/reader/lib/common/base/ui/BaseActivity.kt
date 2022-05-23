package com.lyni.reader.lib.common.base.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.launcher.ARouter
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
import com.lyni.reader.lib.common.base.component.*
import com.lyni.reader.lib.common.databinding.CommonActivityRootBinding
import com.lyni.reader.lib.common.databinding.CommonLayoutCustomHolderBinding
import com.lyni.reader.lib.common.exceptions.BaseException
import com.lyni.treasure.lib.common.ktx.*
import kotlinx.coroutines.delay
import java.lang.reflect.ParameterizedType

/**
 * @date 2022/2/14
 * @author Liangyong Ni
 * description activity基类
 */
abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity(), Initializer, PageStatusHandler {
    protected lateinit var binding: VB
    protected lateinit var viewModel: VM
    private lateinit var rootBinding: CommonActivityRootBinding
    private val type = javaClass.genericSuperclass

    private var currentStatus: Int = STATUS_DEFAULT
    private val dialog: StatusDialog by lazy { StatusDialog(this) }
    private val customHolderView: CommonLayoutCustomHolderBinding by lazy { CommonLayoutCustomHolderBinding.inflate(layoutInflater) }
    private val toolbarConfig: ToolbarConfig by lazy { ToolbarConfig(getToolbarBinding()) }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootBinding = CommonActivityRootBinding.inflate(layoutInflater)
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[0] as Class<VB>
            val method = clazz.getMethod("inflate", LayoutInflater::class.java)
            binding = method.invoke(null, layoutInflater) as VB
            viewModel = ViewModelProvider(this).get(type.actualTypeArguments[1] as Class<VM>)
        }
        rootBinding.flMain.addView(binding.root, 0)
        setContentView(rootBinding.root)
        initData()
        initViewModel(viewModel)
        initView()
        initLiveData()
        initListener()
    }

    /**
     * 初始化View
     */
    override fun initView() {
        rootBinding.refreshHeader.setAssetsFileName("lottie_refresh.json")
        rootBinding.refreshFooter.setAssetsFileName("lottie_refresh.json")
        toolbarConfig().setBack { super.onBackPressed() }
        // 沉浸式状态栏和导航栏
        immersiveStatusBar()
        immersiveNavigationBar()
        fitStatusBar(true)
        fitNavigationBar(true)
    }

    /**
     * 初始化LiveData
     */
    override fun initLiveData() {
        viewModel.isLoadingLiveData().observe(this) {
            it.positive { showLoading() }.otherwise { dismissLoading() }
        }
        viewModel.isEmptyLiveData().observe(this) {
            it.positive { showEmpty() }.otherwise { dismissEmpty() }
        }
        viewModel.errorLiveData().observe(this) {
            if (it is BaseException) {
                showWarning(it.msg())
            } else {
                it?.let { showError() } ?: dismissError()
            }
        }
    }

    /**s
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
     * 隐藏toolbar
     */
    protected fun hideToolbar() {
        rootBinding.toolbar.root.gone()
        val clParams = rootBinding.srlRoot.layoutParams as ConstraintLayout.LayoutParams
        clParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        clParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        clParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        clParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        rootBinding.srlRoot.layoutParams = clParams
    }

    /**
     * 提供刷新View引用
     */
    protected fun getRefreshLayout() = rootBinding.srlRoot

    /**
     * 提供Toolbar View引用
     */
    private fun getToolbarBinding() = rootBinding.toolbar

    protected fun setRefreshHeaderBackground(@ColorInt color: Int? = null, @DrawableRes res: Int? = null) {
        color?.let { rootBinding.refreshHeader.setBackgroundColor(it) }
        res?.let { rootBinding.refreshHeader.setBackgroundResource(res) }
    }

    protected fun setRefreshFooterBackground(@ColorInt color: Int? = null, @DrawableRes res: Int? = null) {
        color?.let { rootBinding.refreshFooter.setBackgroundColor(it) }
        res?.let { rootBinding.refreshFooter.setBackgroundResource(res) }
    }

    protected fun setRootBackground(@ColorInt color: Int? = null, @DrawableRes res: Int? = null) {
        color?.let { rootBinding.srlRoot.setBackgroundColor(it) }
        res?.let { rootBinding.srlRoot.setBackgroundResource(res) }
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
     * 替换main FrameLayout中的内容
     * @param view View
     */
    private fun replaceMainView(view: View) {
        rootBinding.flMain.removeAllViews()
        rootBinding.flMain.addView(view, 0)
        getRefreshLayout().apply {
            isRefreshing.positive {
                getRefreshLayout().finishRefresh()
            }
        }
    }

    /**
     * 设置CustomHolderView
     * @param holder CustomHolder
     */
    private fun setCustomHolderView(holder: CustomHolder) {
        customHolderView.apply {
            tvTitle.text = holder.title
            tvDesc.text = holder.description
            holder.imageRes?.getDrawable?.let {
                ivPlaceHolder.setImageDrawable(it)
            }
            holder.showButton.positive {
                btnAction.visible()
                btnAction.text = holder.btnText
                btnAction.click { holder.event.invoke() }
            }.otherwise {
                btnAction.gone()
            }
        }
    }

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
     * 刷新操作
     */
    open fun refresh() {
        if (currentStatus != STATUS_DEFAULT) {
            currentStatus = STATUS_DEFAULT
            replaceMainView(binding.root)
        }
        getRefreshLayout().apply {
            isRefreshing.positive {
                getRefreshLayout().finishRefresh()
            }
        }
    }

    /**
     * 配置ToolBar
     */
    open fun toolbarConfig() = toolbarConfig

    /**
     * 跳转其他activity
     * @param target 目标
     * @param data 数据
     * @param finish 是否结束当前activity
     */
    fun navigateTo(target: String, data: Bundle? = null, finish: Boolean = false) {
        ARouter.getInstance().build(target)
            .with(data)
            .navigation(this)
        finish.positive { finish() }
    }

    inline fun <reified T : Activity> navigateTo(isFinish: Boolean = false) {
        startActivity(Intent(this, T::class.java))
        isFinish.positive {
            finish()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                assert(v != null)
                imm.hideSoftInputFromWindow(v!!.windowToken, 0)
                v.clearFocus()
            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent
        return window.superDispatchTouchEvent(ev) || onTouchEvent(ev)
    }

    open fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }

    fun showDialog(desc: String, type: DialogType) {
        dialog.show(desc, type)
    }

    fun showWarning(desc: String, duration: Long = 2000L) {
        safeLaunch {
            dialog.show(desc, DialogType.Warning)
            delay(duration)
            dialogDismiss()
        }
    }

    fun showDefault(desc: String, duration: Long = 2000L) {
        safeLaunch {
            dialog.show(desc, DialogType.Default)
            delay(duration)
            dialogDismiss()
        }
    }

    fun showSuccess(desc: String, duration: Long = 2000L) {
        safeLaunch {
            dialog.show(desc, DialogType.Success)
            delay(duration)
            dialogDismiss()
        }
    }

    private fun dialogDismiss() {
        dialog.dismiss()
    }

    protected fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        window.peekDecorView()?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}