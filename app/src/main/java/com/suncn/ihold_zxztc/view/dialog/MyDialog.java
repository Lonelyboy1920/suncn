package com.suncn.ihold_zxztc.view.dialog;


import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.utils.MikeEditTextUtil;

public class MyDialog extends DialogFragment {
    private View mRootView;
    private onItemClickListener onItemClickListener;
    private TextView btn_submit;
    private EditText inputComment;
    private TextWatcher textChangeListener;

    private String myHint;
    private String myText;
    private GITextView tv_mike;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
    }

    private DialogInterface.OnDismissListener mOnClickListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        this.mOnClickListener = listener;
    }

    //做一些弹框的初始化，以及创建一个弹框

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        inputComment.setEnabled(true);
        inputComment.setFocusable(true);
        inputComment.setFocusableInTouchMode(true);
        //inputComment.setCursorVisible(true);
        inputComment.requestFocus();
        if (mOnClickListener != null) {
            mOnClickListener.onDismiss(dialog);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeSoft();
    }

    public void onResume() {
        super.onResume();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(lp);
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        //getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager inManager = (InputMethodManager) inputComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 100);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //对话框的布局
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.popup_circle_comment_add, container, false);
        }
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mRootView.setLayoutParams(layoutParams);
        inputComment = mRootView.findViewById(R.id.et_discuss);
        if (GIStringUtil.isBlank(myHint)) {
            myHint = getString(R.string.string_friendly_commen_is_the_starting_point_of_communication);
        }
        inputComment.setHint(myHint);
        inputComment.setEnabled(true);
        inputComment.setFocusable(true);
        inputComment.setFocusableInTouchMode(true);
        inputComment.setText(myText);
        //inputComment.setCursorVisible(true);
        inputComment.requestFocus();
        btn_submit = mRootView.findViewById(R.id.tv_confirm);
        tv_mike = mRootView.findViewById(R.id.tv_mike);
        if (GIStringUtil.isNotBlank(myText)) {
            btn_submit.setTextColor(getActivity().getResources().getColor(R.color.view_head_bg));
        }
//        btn_submit.setText("评论");

        //绑定监听事件
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在activity里面处理点击事件
                if (GIStringUtil.isNotBlank(inputComment.getText().toString().trim()))
                    onItemClickListener.onItemClick(v, inputComment, inputComment.getText().toString().trim());
                closeSoft();
            }
        });
        tv_mike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MikeEditTextUtil util = new MikeEditTextUtil(getActivity(), inputComment);
                util.viewShow();
            }
        });
        inputComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (GIStringUtil.isNotBlank(editable.toString())) {
                    btn_submit.setTextColor(getActivity().getResources().getColor(R.color.view_head_bg));
                } else {
                    btn_submit.setTextColor(getActivity().getResources().getColor(R.color.font_source));
                }
            }
        });
        return mRootView;
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

    private void closeSoft() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager.isActive()) {
            manager.hideSoftInputFromWindow(mRootView.getApplicationWindowToken(), 0);
        }
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
