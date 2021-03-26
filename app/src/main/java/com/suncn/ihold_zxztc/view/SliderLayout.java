package com.suncn.ihold_zxztc.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import com.google.android.material.tabs.TabLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.suncn.ihold_zxztc.R;

import java.lang.ref.WeakReference;

public class SliderLayout extends LinearLayout {
    private int totalNum = 0;
    private ImageView mSlider;
    private Drawable mSliderImage;
    private WeakReference<TabLayout> mTabLayoutRef;

    public SliderLayout(Context context) {
        this(context, null);
    }

    public SliderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SliderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SliderLayout);
        mSliderImage = array.getDrawable(R.styleable.SliderLayout_slider_pic);
        if (mSliderImage == null) {
            mSliderImage = context.getResources().getDrawable(R.drawable.bg_lxtj);
        }
        array.recycle();
        init(context);
    }

    private void init(Context context) {
        mSlider = new ImageView(context);
        mSlider.setImageDrawable(mSliderImage);
        addView(mSlider, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resetSlider();
    }

    /**
     * 重新设置滑块
     */
    private void resetSlider() {
        if (getOrientation() == HORIZONTAL) {
            resetHorizontalSlider();
        }
    }

    /**
     * 重置水平方向的滑块大小
     */
    private void resetHorizontalSlider() {
        if (mTabLayoutRef == null) return;
        TabLayout tabLayout = mTabLayoutRef.get();
        if (tabLayout == null) return;
        LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
        totalNum = mTabStrip.getChildCount();
        if (totalNum > 0) {
            View firstView = mTabStrip.getChildAt(0);
            int width = firstView.getMeasuredWidth();
            resetSlider(width);
        }
    }

    //重新设置滑块的大小
    private void resetSlider(int width) {
        LayoutParams params = (LayoutParams) mSlider.getLayoutParams();
        params.width = width;//重新设置滑块的大小
        params.height = getHeight() / 2;
        params.gravity = Gravity.CENTER_VERTICAL;
        mSlider.setPadding(width / 10, 0, width / 10, 0);//设置View的左右向内收缩
        mSlider.setLayoutParams(params);
    }

    public void setupWithTabLayout(TabLayout tabLayout) {
        mTabLayoutRef = new WeakReference<>(tabLayout);
        resetHorizontalSlider();
    }

    public static final String TAG = SliderLayout.class.getName();

    public static class SliderOnPageChangeListener extends TabLayoutOnPageChangeListener {
        private final WeakReference<SliderLayout> mSliderLayoutRef;

        public SliderOnPageChangeListener(TabLayout tabLayout, SliderLayout layout) {
            super(tabLayout);
            mSliderLayoutRef = new WeakReference<SliderLayout>(layout);
            layout.setupWithTabLayout(tabLayout);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            final SliderLayout layout = mSliderLayoutRef.get();
            if (layout != null) {
                layout.setScrollPosition(position, positionOffset);
            }
        }
    }

    /**
     * 把滑块滑动到指定的位置
     *
     * @param position       当前位置
     * @param positionOffset 滑动到下一个或上一个位置比例
     */
    private void setScrollPosition(int position, float positionOffset) {
        final int roundedPosition = Math.round(position + positionOffset);
        if (roundedPosition < 0 || roundedPosition >= totalNum) {
            return;
        }
        float scrollX = calculateScrollXForTab(position, positionOffset);
        scrollTo((int) scrollX, 0);
    }

    /**
     * 计算滑块需要滑动的距离
     *
     * @param position       当前选择的位置
     * @param positionOffset 滑动位置的百分百
     * @return 滑动的距离
     */
    private int calculateScrollXForTab(int position, float positionOffset) {
        TabLayout tabLayout = mTabLayoutRef.get();
        if (tabLayout == null) return 0;
        LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
        if (mTabStrip == null) return 0;
//当前选择的View
        final View selectedChild = mTabStrip.getChildAt(position);
//下一个View
        final View nextChild = position + 1 < mTabStrip.getChildCount()
                ? mTabStrip.getChildAt(position + 1)
                : null;
//当前选择的View的宽度
        final int selectedWidth = selectedChild != null ? selectedChild.getWidth() : 0;
//下一个View的宽度
        final int nextWidth = nextChild != null ? nextChild.getWidth() : 0;
//当前选择的View的左边位置，view的方位
        final int left = selectedChild != null ? selectedChild.getLeft() : 0;
//计算滑块需要滑动的距离,左 + ，右 - ；
        int scrollX = -(left + ((int) ((selectedWidth + nextWidth) * positionOffset * 0.5f)));
        if (tabLayout.getTabMode() == TabLayout.MODE_SCROLLABLE) {//当为滑动模式的时候TabLayout会有水平方向的滑动
            scrollX += tabLayout.getScrollX();//计算在TabLayout有滑动的时候，滑块相对的滑动距离
        }
        return scrollX;
    }
  /*  private int totalNum = 0;
    private ImageView mSlider;
    private Drawable mSliderImage;
    private TabLayout tabLayout;

    public SliderLayout(Context context) {
        super(context);
        init(context);
    }

    public Drawable getSliderImage() {
        return mSliderImage;
    }

    public void setSliderImage(Drawable sliderImage) {
        mSliderImage = sliderImage;
        mSlider.setImageDrawable(mSliderImage);
    }

    private void init(Context context) {
        //mSliderImage = ContextCompat.getDrawable(context, R.mipmap.bg_lxtj_hover);
        mSlider = new ImageView(context);
        mSlider.setImageDrawable(mSliderImage);
        addView(mSlider, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resetSlider();
    }

    *//**
     * 重新设置滑块
     *//*
    private void resetSlider() {
        LinearLayout mTabStrip = getTabStrip();
        if (mTabStrip == null) return;
        totalNum = mTabStrip.getChildCount();
        if (totalNum > 0) {
            View firstView = mTabStrip.getChildAt(0);
            int width = firstView.getMeasuredWidth();
            resetSlider(width);
        }
    }

    private LinearLayout getTabStrip() {
        if (tabLayout == null) return null;
        if (tabLayout instanceof MyTabLayout) {
            // return ((MyTabLayout) tabLayout).getTabStrip();
            return null;
        } else {
            return (LinearLayout) tabLayout.getChildAt(0);
        }
    }

    private void resetSlider(int width) {
        LayoutParams params = (LayoutParams) mSlider.getLayoutParams();
        params.width = width;
        //重新设置滑块的大小
        params.height = getHeight() / 2;
        params.gravity = Gravity.CENTER_VERTICAL;
        mSlider.setPadding(width / 10, 0, width / 10, 0);
        mSlider.setLayoutParams(params);
    }

    public void setupWithTabLayout(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
        resetSlider();
    }

    *//**
     * 把滑块滑动到指定的位置
     *
     * @param position       当前位置
     * @param positionOffset 滑动到下一个或上一个位置比例
     *//*
    private void setScrollPosition(int position, float positionOffset) {
        final int roundedPosition = Math.round(position + positionOffset);
        if (roundedPosition < 0 || roundedPosition >= totalNum) {
            return;
        }
        float scrollX = calculateScrollXForTab(position, positionOffset);
        scrollTo((int) scrollX, 0);
    }

    *//**
     * 计算滑块需要滑动的距离
     *
     * @param position       当前选择的位置
     * @param positionOffset 滑动位置的百分百
     * @return 滑动的距离
     *//*
    private int calculateScrollXForTab(int position, float positionOffset) {
        //        TabLayout tabLayout = mTabLayoutRef.get();
        if (tabLayout == null) return 0;
        LinearLayout mTabStrip = getTabStrip();
        if (mTabStrip == null)
            return 0;
        //当前选择的View
        final View selectedChild = mTabStrip.getChildAt(position);
        //下一个View
        final View nextChild = position + 1 < mTabStrip.getChildCount() ? mTabStrip.getChildAt(position + 1) : null;
        //当前选择的View的宽度
        final int selectedWidth = selectedChild != null ? selectedChild.getWidth() : 0;
        //下一个View的宽度
        final int nextWidth = nextChild != null ? nextChild.getWidth() : 0;
        //当前选择的View的左边位置，view的方位
        final int left = selectedChild != null ? selectedChild.getLeft() : 0;
        //计算滑块需要滑动的距离,左 + ，右 - ；
        int scrollX = -(left + ((int) ((selectedWidth + nextWidth) * positionOffset * 0.5f)));
        //现在是把SliderLayout直接放在TableLayout中的所以就不许要考虑TableLayout本身的滑动
         *//*if (tabLayout.getTabMode() == TabLayout.MODE_SCROLLABLE) {//当为滑动模式的时候TabLayout会有水平方向的滑动
        scrollX += tabLayout.getScrollX();//计算在TabLayout有滑动的时候，滑块相对的滑动距离
    }*//*
        return scrollX;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
        resetSlider();
    }

    public static class SliderOnPageChangeListener extends TabLayoutOnPageChangeListener {
        private final SoftReference<SliderLayout> mSliderLayoutRef;

        public SliderOnPageChangeListener(TabLayout tabLayout, SliderLayout layout) {
            super(tabLayout);
            mSliderLayoutRef = new SoftReference<SliderLayout>(layout);
            layout.setupWithTabLayout(tabLayout);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            final SliderLayout layout = mSliderLayoutRef.get();
            if (layout != null) {
                layout.setScrollPosition(position, positionOffset);
            }
        }
    }*/

}
