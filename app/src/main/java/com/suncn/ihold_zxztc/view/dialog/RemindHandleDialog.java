package com.suncn.ihold_zxztc.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.suncn.ihold_zxztc.R;

/**
 * 报告迟到原因对话框
 */
public class RemindHandleDialog extends Dialog {
    public EditText content_EditText;
    private TextView title_TextView;
    private TextView cancel_TextView;
    private TextView confirm_TextView;
    private Activity activity;

    public EditText getContent_EditText() {
        return content_EditText;
    }

    public RemindHandleDialog(Activity activity) {
        super(activity, R.style.style_dialog);
        this.activity = activity;
        setContentView(R.layout.dialog_remind_handle);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        title_TextView = findViewById(R.id.tv_title);
        content_EditText = findViewById(R.id.et_content);
        cancel_TextView = findViewById(R.id.tv_cancel);
        confirm_TextView = findViewById(R.id.tv_confirm);
    }

    /**
     * 确定键监听器
     */
    public void setOnPositiveListener(View.OnClickListener listener) {
        confirm_TextView.setOnClickListener(listener);
    }

    /**
     * 取消键监听器
     */
    public void setOnNegativeListener(View.OnClickListener listener) {
        cancel_TextView.setOnClickListener(listener);
    }

}
