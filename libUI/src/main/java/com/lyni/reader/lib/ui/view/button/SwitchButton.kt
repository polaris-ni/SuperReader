package com.lyni.reader.lib.ui.view.button

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.lyni.reader.lib.ui.R
import com.lyni.reader.lib.ui.listener.SwitchListener
import com.lyni.treasure.lib.common.ktx.getColor
import com.lyni.treasure.lib.common.ktx.negative

/**
 * @author Liangyong Ni
 * description SwitchButton
 * @date 2022/5/26
 */
class SwitchButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr), View.OnClickListener {
    private var mCheckColor = R.color.color_primary.getColor
    private var mUnCheckColor = R.color.color_bg_gray.getColor
    private val mOutPaint: Paint
    private val mSwitchPaint: Paint
    private val mDrawRoundRectF = RectF()
    private var mCheck = false
    private var mRx = 0
    private var mSwitchListener: SwitchListener? = null
    private val mPending = 4

    init {
        setOnClickListener(this)
        var mSwitchColor = Color.WHITE
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton)
            mCheckColor = typedArray.getColor(R.styleable.SwitchButton_check_color, R.color.color_primary.getColor)
            mUnCheckColor = typedArray.getColor(R.styleable.SwitchButton_uncheck_color, R.color.color_bg_gray.getColor)
            mSwitchColor = typedArray.getColor(R.styleable.SwitchButton_switch_color, R.color.white.getColor)
            mCheck = typedArray.getBoolean(R.styleable.SwitchButton_checked, false)
            isClickable = typedArray.getBoolean(R.styleable.SwitchButton_enabled, true)
            typedArray.recycle()
        }
        mOutPaint = Paint()
        mSwitchPaint = Paint()
        mOutPaint.color = mUnCheckColor
        mOutPaint.isAntiAlias = true
        mSwitchPaint.color = mSwitchColor
        mSwitchPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        mDrawRoundRectF[0f, 0f, width.toFloat()] = height.toFloat()
        val mRadius = height / 2 - dipToPx(mPending)
        isClickable.negative {
            mOutPaint.alpha = 10
        }
        mOutPaint.color = if (mCheck) mCheckColor else mUnCheckColor
        canvas.drawRoundRect(
            mDrawRoundRectF, height / 2.0f,
            height / 2.0f, mOutPaint
        )
        if (mRx == 0) {
            mRx = if (!mCheck) {
                mRadius + dipToPx(mPending)
            } else {
                width - height / 2
            }
        }
        canvas.drawCircle(mRx.toFloat(), height / 2.0f, mRadius.toFloat(), mSwitchPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.AT_MOST) { //warp_content 给个默认的
            width = dipToPx(56)
            height = width / 2
        } else if (heightMode == MeasureSpec.AT_MOST && widthMode == MeasureSpec.EXACTLY) {
            height = width / 2
        }
        height = (width / 2).coerceAtMost(height) //高度不能大于 宽度的一半
        setMeasuredDimension(width, height)
    }

    private fun dipToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }

    private fun drawAnim() {
        val valueAnimator = ObjectAnimator.ofInt(
            if (mCheck) height / 2 else width - height / 2, if (mCheck) width - height / 2 else height / 2
        )
        valueAnimator.interpolator = AccelerateInterpolator()
        valueAnimator.duration = 192
        valueAnimator.addUpdateListener { animation: ValueAnimator ->
            mRx = animation.animatedValue as Int
            invalidate()
        }
        valueAnimator.start()
        if (mSwitchListener != null) {
            mSwitchListener!!.changeCheck(mCheck)
        }
    }

    fun setOnSwitchListener(switchListener: SwitchListener) {
        mSwitchListener = switchListener
    }

    override fun onClick(v: View) {
        if (isClickable) {
            mCheck = !mCheck
            drawAnim()
        }
    }

    fun setSwitchColor(checkColor: Int, unCheckColor: Int) {
        mCheckColor = checkColor
        mUnCheckColor = unCheckColor
        invalidate()
    }

    fun setEnable(isEnabled: Boolean) {
        isClickable = isEnabled
        setOnClickListener(this)
    }

    fun setChecked(isCheck: Boolean) {
        mCheck = isCheck
        drawAnim()
    }
}