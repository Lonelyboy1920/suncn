package com.qd.longchat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/19 上午10:07
 */
public class QDVideoView extends VideoView {

    public QDVideoView(Context context) {
        super(context);
    }

    public QDVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QDVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 其实就是在这里做了一些处理。
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
