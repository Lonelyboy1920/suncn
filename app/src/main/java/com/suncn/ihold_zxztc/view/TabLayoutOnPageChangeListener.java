package com.suncn.ihold_zxztc.view;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;

import java.lang.ref.WeakReference;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE;

public class TabLayoutOnPageChangeListener implements ViewPager.OnPageChangeListener {
    private final WeakReference<TabLayout> mTabLayoutRef;
    private int mPreviousScrollState;
    private int mScrollState;

    public TabLayoutOnPageChangeListener(TabLayout tabLayout) {
        mTabLayoutRef = new WeakReference<>(tabLayout);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mPreviousScrollState = mScrollState;
        mScrollState = state;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { //省略
    }


    @Override
    public void onPageSelected(int position) { //省略
    }

    private void reset() {
        mPreviousScrollState = mScrollState = SCROLL_STATE_IDLE;
    }
}
