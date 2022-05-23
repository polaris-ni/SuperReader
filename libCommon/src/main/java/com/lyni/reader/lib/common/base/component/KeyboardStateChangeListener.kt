package com.lyni.reader.lib.common.base.component

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.lyni.treasure.lib.common.utils.Log

/**
 * @date 2022/5/11
 * @author Liangyong Ni
 * description KeyboardStateChangeListener
 */
class KeyboardStateChangeListener(private var activity: FragmentActivity? = null, private val onChange: (keyboardHeight: Int) -> Unit) :
    ViewTreeObserver.OnGlobalLayoutListener, ProactiveLifecycleObserver {
    companion object {
        private const val TAG = "KeyboardStateChangeListener"
    }

    private var mWindowHeight = 0

    override fun onGlobalLayout() {
        val r = Rect()
        //获取当前窗口实际的可见区域
        requireActivity().window.decorView.getWindowVisibleDisplayFrame(r)
        val height = r.height()
        if (mWindowHeight == 0) {
            //一般情况下，这是原始的窗口高度
            mWindowHeight = height
        } else {
            //两次窗口高度相减，就是软键盘高度
            val softKeyboardHeight = mWindowHeight - height
            onChange.invoke(softKeyboardHeight)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun addListener() {
        requireActivity().window.decorView.viewTreeObserver.addOnGlobalLayoutListener(this)
        Log.d(TAG, "listener is added")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun removeListener() {
        requireActivity().window.decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        activity = null
        Log.d(TAG, "listener is removed")
    }

    private fun requireActivity(): FragmentActivity {
        if (activity == null) {
            throw RuntimeException("Activity is not attach to KeyboardStateChangeListener")
        }
        return activity!!
    }
}