package com.suncn.ihold_zxztc.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 表单显示编辑选择控件，可自定义字体大小和颜色以及icon
 */
public class MenuItemEditLayout extends FrameLayout {
    private Context mContext;
    private View mView;
    private TextView tvLabel;
    private TextView tvValue;
    private GITextView tvArrow;
    private LinearLayout llLine;
    private String label;
    private String value;
    private OnClickListener onClickListener;

    public MenuItemEditLayout(Context context) {
        this(context, null);
    }

    public MenuItemEditLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuItemEditLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.item_menu_edit_layout, this, true);
        tvLabel = mView.findViewById(R.id.tv_label);
        tvValue = mView.findViewById(R.id.tv_value);
        tvArrow = mView.findViewById(R.id.tv_arrow);
        llLine = mView.findViewById(R.id.ll_line);
        TypedArray a = mContext.obtainStyledAttributes(attributeSet, R.styleable.MenuItemEditLayout);
        setLabel(a.getString(R.styleable.MenuItemEditLayout_label));
        setValue(a.getString(R.styleable.MenuItemEditLayout_value));
        //是否显示右边的图标
        if (a.getBoolean(R.styleable.MenuItemEditLayout_show_arrow, false)) {
            tvArrow.setVisibility(VISIBLE);
        } else {
            tvArrow.setVisibility(GONE);
        }
        //设置hint
        if (GIStringUtil.isNotBlank(a.getString(R.styleable.MenuItemEditLayout_hint))) {
            tvValue.setHint(a.getString(R.styleable.MenuItemEditLayout_hint));
        } else {
            tvValue.setHint(mContext.getResources().getString(R.string.string_please_select) + a.getString(R.styleable.MenuItemEditLayout_label));
        }
        //设置图标样式
        if (GIStringUtil.isNotBlank(a.getString(R.styleable.MenuItemEditLayout_right_icon))) {
            tvArrow.setText(a.getString(R.styleable.MenuItemEditLayout_right_icon));
        }
        //是否显示下划线
        if (a.getBoolean(R.styleable.MenuItemLayout_show_line, false)) {
            llLine.setVisibility(VISIBLE);
        } else {
            llLine.setVisibility(GONE);
        }
        tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimension(R.styleable.MenuItemEditLayout_label_size, getResources().getDimension(R.dimen.font_15)));
        tvLabel.setTextColor(a.getColor(R.styleable.MenuItemEditLayout_label_color, context.getResources().getColor(R.color.font_title)));
        tvValue.setTextColor(a.getColor(R.styleable.MenuItemEditLayout_value_color, context.getResources().getColor(R.color.font_title)));
        tvValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimension(R.styleable.MenuItemEditLayout_value_size, getResources().getDimension(R.dimen.font_15)));

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

    public String getHint() {
        return tvValue.getHint().toString();
    }


    private void setViewOnlickListener(OnClickListener onlickListener) {
        this.onClickListener = onlickListener;
        mView.setOnClickListener(onClickListener);
    }
}
