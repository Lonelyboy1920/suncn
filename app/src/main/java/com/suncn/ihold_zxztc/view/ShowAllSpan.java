package com.suncn.ihold_zxztc.view;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import com.suncn.ihold_zxztc.R;

/**
 * Created by yang1006 on 17/7/13.
 * 显示全文的span
 */
public class ShowAllSpan extends ClickableSpan {
    private OnAllSpanClickListener clickListener;
    private boolean isPressed = false;
    private Context context;

    public ShowAllSpan(Context context, OnAllSpanClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View widget) {
        if (clickListener != null) {
            clickListener.onClick(widget);
        }
    }

    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }

    public interface OnAllSpanClickListener {
        void onClick(View widget);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        if (isPressed) {
            ds.bgColor = context.getResources().getColor(R.color.line_bg_white);
        } else {
            ds.bgColor = context.getResources().getColor(android.R.color.transparent);
        }
        ds.setColor(context.getResources().getColor(R.color.view_head_bg));
        ds.setUnderlineText(false);
    }
}
