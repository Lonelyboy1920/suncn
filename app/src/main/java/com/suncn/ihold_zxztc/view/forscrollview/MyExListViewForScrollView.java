package com.suncn.ihold_zxztc.view.forscrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * 解决ExpandableListView和scrollview的嵌套问题
 */
public class MyExListViewForScrollView extends ExpandableListView {
    public MyExListViewForScrollView(Context context) {
        super(context);
    }

    public MyExListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyExListViewForScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
