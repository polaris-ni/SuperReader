package com.lyni.reader.lib.common.base.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.lyni.reader.lib.common.R;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;

/**
 * @author Liangyong Ni
 * @date 2022/3/4
 * description 刷新脚
 */
@SuppressLint("CustomViewStyleable")
public class CustomRefreshFooter extends SimpleComponent implements RefreshFooter {

    protected LottieAnimationView mLottieView;
    protected String mAssetsFileName;
    protected FrameLayout bg;
    protected boolean mNoMoreData = false;

    public CustomRefreshFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRefreshFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CustomRefresh);
        mAssetsFileName = array.getString(R.styleable.CustomRefresh_assetsName);
        array.recycle();
        View.inflate(context, R.layout.common_layout_refresh, this);
        View thisView = this;
        mLottieView = thisView.findViewById(R.id.lottieFooter);
        bg = thisView.findViewById(R.id.bg);
        mLottieView.setVisibility(VISIBLE);
    }

    public void setAssetsFileName(String name) {
        mAssetsFileName = name;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        if (!mNoMoreData) {
            switch (newState) {
                case None:
                    mLottieView.setFrame(0);
                    mLottieView.setProgress(0.0F);
                    break;
                case PullUpToLoad:
                    if (!TextUtils.isEmpty(this.mAssetsFileName)) {
                        mLottieView.setVisibility(VISIBLE);
                        if (!mLottieView.isAnimating()) {
                            mLottieView.setAnimation(mAssetsFileName);
                            mLottieView.playAnimation();
                        }
                    }
                    break;
                case PullUpCanceled:
                    //没有更多数据
                    break;
                case LoadFinish:
                    mLottieView.setVisibility(GONE);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final LottieAnimationView lottieView = mLottieView;
        if (lottieView.getVisibility() == VISIBLE) {
            lottieView.cancelAnimation();
        }
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        if (mNoMoreData != noMoreData) {
            mNoMoreData = noMoreData;
            if (mLottieView != null) {
                mLottieView.cancelAnimation();
                mLottieView.setFrame(0);
                mLottieView.setProgress(0.0F);
                mLottieView.setVisibility(GONE);
            }
        }
        return true;
    }
}

