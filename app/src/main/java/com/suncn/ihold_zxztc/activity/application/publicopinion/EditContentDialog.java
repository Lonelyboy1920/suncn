package com.suncn.ihold_zxztc.activity.application.publicopinion;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.suncn.ihold_zxztc.R;

public class EditContentDialog extends Dialog {
    private View mRootView;
    private onItemClickListener onItemClickListener;
    private TextView btn_submit;
    private TextView btn_cancel;
    private EditText inputComment;
    private TextWatcher textChangeListener;
    private TextView tv_count;

    private String myHint;
    private String myText;
    private String myTitle;
    private Activity activity;
    private TextView tv_title;
    private int maxLength;


    public EditContentDialog(Activity activity, int themeResId) {
        super(activity, themeResId);
        this.activity = activity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_edit_content);
        btn_submit = findViewById(R.id.tv_confirm);
        btn_cancel = findViewById(R.id.tv_cancel);
        inputComment = findViewById(R.id.et_discuss);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(myTitle);
        inputComment.setHint(myHint);
        inputComment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        //绑定监听事件
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在activity里面处理点击事件
                onItemClickListener.onItemClick(v, inputComment, inputComment.getText().toString().trim());
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        show(this);
    }

    private void show(EditContentDialog mDialog) {
        if (mDialog != null) {
            Window window = mDialog.getWindow();
            window.setGravity(Gravity.CENTER);
            window.getDecorView().setPadding(0, 0, 0, 0);
            mDialog.setCanceledOnTouchOutside(false);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);  //将设置好的属性set回去
        }
    }


    private OnDismissListener mOnClickListener;

    public void setOnDismissListener(OnDismissListener listener) {
        this.mOnClickListener = listener;
    }

    public void setMyTitle(String myTitle) {
        this.myTitle = myTitle;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setMyHint(String myHint) {
        this.myHint = myHint;
    }

    public void setMyText(String myText) {
        this.myText = myText;
    }

    public TextView getBtn_submit() {
        return btn_submit;
    }

    public EditText getInputComment() {
        return inputComment;
    }


    //监听事件接口
    public interface onItemClickListener {
        void onItemClick(View v, EditText e, String content);
    }


    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setTextChangeListener(TextWatcher textChangeListener) {
        this.textChangeListener = textChangeListener;
    }
}
