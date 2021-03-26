package com.suncn.ihold_zxztc.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 表单显示控件，可自定义字体大小和颜色
 */
public class MenuItemLayout extends FrameLayout {
    private Context mContext;
    private View mView;
    private TextView tvLabel;
    private TextView tvValue;
    private GITextView tvArrow;
    private String label;
    private String value;
    private OnClickListener onClickListener;

    public MenuItemLayout(Context context) {
        this(context, null);
    }

    public MenuItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuItemLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.item_menu_layout, this, true);
        tvLabel = mView.findViewById(R.id.tv_label);
        tvValue = mView.findViewById(R.id.tv_value);
        tvArrow = mView.findViewById(R.id.tv_arrow);
        TypedArray a = mContext.obtainStyledAttributes(attributeSet, R.styleable.MenuItemLayout);
        setLabel(a.getString(R.styleable.MenuItemLayout_label));
        setValue(a.getString(R.styleable.MenuItemLayout_value));
        if (a.getBoolean(R.styleable.MenuItemLayout_show_arrow, false)) {
            tvArrow.setVisibility(VISIBLE);
        } else {
            tvArrow.setVisibility(GONE);
        }
        tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimension(R.styleable.MenuItemLayout_label_size, getResources().getDimension(R.dimen.font_15)));
        tvLabel.setTextColor(a.getColor(R.styleable.MenuItemLayout_label_color, context.getResources().getColor(R.color.font_source)));
        tvValue.setTextColor(a.getColor(R.styleable.MenuItemLayout_value_color, context.getResources().getColor(R.color.font_title)));
        tvValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimension(R.styleable.MenuItemLayout_value_size, getResources().getDimension(R.dimen.font_15)));

    }

    public void setLabel(String label) {
        if (tvLabel != null) {
            this.label = label;
            tvLabel.setText(label);
        }
    }

    public void setValue(String value) {
        if (tvValue != null) {
            this.value = value;
            tvValue.setText(value);
        }
    }

    public String getValue() {
        if (tvValue != null) {
            return tvValue.getText().toString();
        }
        return value;
    }

    private void setViewOnlickListener(OnClickListener onlickListener) {
        this.onClickListener = onlickListener;
        mView.setOnClickListener(onClickListener);
    }
}
