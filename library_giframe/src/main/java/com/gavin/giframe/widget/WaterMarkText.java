package com.gavin.giframe.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;

import com.gavin.giframe.R;
import com.gavin.giframe.utils.GISharedPreUtil;

/**
 * 水印文字
 */
public class WaterMarkText extends androidx.appcompat.widget.AppCompatTextView {
    private int mDegree; // 旋转角度

    public WaterMarkText(Context context) {
        this(context, null);
    }

    public WaterMarkText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterMarkText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setGravity(Gravity.CENTER);
        // 获取自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaterMarkText, defStyleAttr, 0);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int index = ta.getIndex(i);
            if (index == R.styleable.WaterMarkText_degree) {
                mDegree = ta.getInt(index, 0);
            }
        }
        setText(GISharedPreUtil.getString(context, "USER") + "/" + GISharedPreUtil.getString(context, "strCname"));
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + 80);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        // 位移，保持文字居中
        canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
        // 以文字中心轴旋转
        canvas.rotate(mDegree, this.getWidth() / 2f, this.getHeight() / 2f);
        super.onDraw(canvas);
        canvas.restore();
    }

    /**
     * 设置旋转角度
     *
     * @param degree
     */
    public void setDegree(int degree) {
        this.mDegree = degree;
    }
}

