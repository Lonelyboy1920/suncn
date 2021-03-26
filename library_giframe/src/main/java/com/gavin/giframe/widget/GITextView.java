package com.gavin.giframe.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;

import com.gavin.giframe.R;
import com.gavin.giframe.utils.GIFontUtil;

import skin.support.widget.SkinCompatTextView;

/**
 * 自定义TextView
 * 1.设置字体样式
 * 2.设置字间距
 */
public class GITextView extends SkinCompatTextView {
    private String type = "1"; //字体类型，1：表示阿里图标库 2：表示思源宋体-标准；3：表示思源宋体-粗体
    private float spacing = 0; // 字间距

    public GITextView(Context context, String strType) {
        super(context);
        type = strType;
        init(context);
    }

    public GITextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GITextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GITextView);
        type = a.getString(R.styleable.GITextView_myStyle);
        spacing = a.getFloat(R.styleable.GITextView_textSpacing, 0);
        a.recycle();
        init(context);
    }

    /**
     * 重置字体样式
     */
    public void refreshFontType(Context context, String strType) {
        type = strType;
        init(context);
    }

    /**
     * 初始化字体
     */
    private void init(Context context) {
        setIncludeFontPadding(false); // 清除TextView字体周边空白
        setTypeface(GIFontUtil.setFont(context, type)); //设置字体样式
        if (spacing > 0) {
            applySpacing();
        }
    }

    /**
     * 设置字间距
     * 在xml中调用app:textSpacing=""
     */
    private void applySpacing() {
        CharSequence originalText = getText();
        if (originalText == null) return;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < originalText.length(); i++) {
            builder.append(originalText.charAt(i));
            if (i + 1 < originalText.length()) {
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if (builder.toString().length() > 1) {
            for (int i = 1; i < builder.toString().length(); i += 2) {
                finalText.setSpan(new ScaleXSpan((spacing + 1) / 10), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        super.setText(finalText, BufferType.SPANNABLE);
    }
}
