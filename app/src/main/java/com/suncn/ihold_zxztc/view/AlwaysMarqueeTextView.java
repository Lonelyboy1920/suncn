package com.suncn.ihold_zxztc.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class AlwaysMarqueeTextView extends TextView {
    
    public AlwaysMarqueeTextView(Context context) {
	super(context);
    }
    
    public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
	super(context, attrs);
    }
    
    public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
    }
    
    @Override
    public boolean isFocused() {
	return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) {//当有焦点的时候 开启动画  没有的时候 什么都不做保持状态
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }
}
