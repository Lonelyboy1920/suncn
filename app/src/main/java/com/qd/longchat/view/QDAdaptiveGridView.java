package com.qd.longchat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by xxw on 2016/7/20.
 */
public class QDAdaptiveGridView extends GridView {

    public QDAdaptiveGridView(Context context) {
        super(context);
    }

    public QDAdaptiveGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QDAdaptiveGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
