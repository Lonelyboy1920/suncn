package com.suncn.ihold_zxztc.view.forscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自动适应高度的ViewPager
 *
 * @author
 */
public class MyViewPagerForScrollView extends NoPreloadViewPager {

    public MyViewPagerForScrollView(Context context) {
        super(context);
    }

    public MyViewPagerForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height)
                height = h;
        }
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    float xDown;
    float yDown;
    float xMove;
    float yMove;
    float offsetX;
    float offsetY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = ev.getX();
                yDown = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = ev.getX();
                yMove = ev.getY();
                offsetX += ev.getX() - xDown;
                offsetY += ev.getY() - yDown;
                xDown = ev.getX();
                yDown = ev.getY();

                if (Math.abs(offsetX) > Math.abs(offsetY) || Math.abs(offsetX) == 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                offsetX = 0;
                offsetY = 0;
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}