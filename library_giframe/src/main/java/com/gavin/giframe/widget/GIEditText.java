package com.gavin.giframe.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.TextView;

import com.gavin.giframe.R;
import com.gavin.giframe.other.filter.ForbidInputEmojiFilter;
import com.gavin.giframe.other.filter.MaxTextLengthFilter;

/**
 * 自定义EditText
 * 1.限制不让输入表情
 * 2.控制最大可输入字数
 * 注意：如果字数有限制，需要使用此控制自定义的属性设置，原edittext的maxlength将不再有效果
 */
public class GIEditText extends androidx.appcompat.widget.AppCompatEditText {
    private Context mContext;
    private int maxLength;

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        initEditText();
    }

    public GIEditText(Context context) {
        super(context);
        this.mContext = context;
        initEditText();
    }

    public GIEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GIEditText);
        maxLength = a.getInteger(R.styleable.GIEditText_maxLength, 100000);
        initEditText();
    }

    public GIEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initEditText();
    }

    private void initEditText() {
        setFilters(new InputFilter[]{new ForbidInputEmojiFilter(), new MaxTextLengthFilter(maxLength)});
    }

    /**
     * 用TextView显示已输入的字数
     */
    public void setTextView(final TextView textView) {
        textView.setText("0/" + maxLength);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textView.setText(s.length() + "/" + maxLength);
            }
        });
    }
}
