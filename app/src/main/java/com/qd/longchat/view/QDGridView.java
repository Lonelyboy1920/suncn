package com.qd.longchat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/13 下午12:01
 */
public class QDGridView extends GridView {

    public QDGridView(Context context) {
        super(context);
    }

    public QDGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QDGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
