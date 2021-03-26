package com.suncn.ihold_zxztc.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.utils.MikeEditTextUtil;
import com.suncn.ihold_zxztc.utils.Utils;

/**
 * 输入意见的对话框
 */
@SuppressWarnings("deprecation")
public class Office_InstructionDialog extends Dialog {
    private Activity activity;
    private TextView send_Button;
    private EditText opinion_EditText;
    private TextView dialogTitle_TextView;
    private int signInType;
    private Typeface iconFont;
    private TextView mike_TextView;
    private TextView cancle_TextView;

    public Office_InstructionDialog(Activity activity) {
        super(activity, R.style.style_dialog);
        this.activity = activity;
        iconFont = Typeface.createFromAsset(activity.getAssets(), "iconfont.ttf");
        setContentView(R.layout.view_dialog_instruction_office_detail);
        initView();
    }

    public EditText getOpinion_EditText() {
        return opinion_EditText;
    }

    public void setIdea(String strIdea) {
        if (strIdea != null) {
            opinion_EditText.setText(strIdea);
        }
    }

    public void setSignInType(int signInType) {
        this.signInType = signInType;
        switch (signInType) {
            case 0: // 0-签收
                dialogTitle_TextView.setText("请输入签收意见");
                break;
            case 1: // 1-拒收
                dialogTitle_TextView.setText("请输入拒收原因");
                break;
            case 2: // 2-回复通知
                dialogTitle_TextView.setText("请输入回复内容");
                break;
            case 3:
                dialogTitle_TextView.setText("请输入地点");
                break;
            case 5: // 5-查看短信内容
                dialogTitle_TextView.setText("查看短信内容");
                mike_TextView.setVisibility(View.GONE);
                cancle_TextView.setVisibility(View.GONE);
                break;
            case 6:
                dialogTitle_TextView.setText("请输入群名称");
                break;
            case 7:
                dialogTitle_TextView.setText("请输入群公告");
                break;
            case 8:
                dialogTitle_TextView.setText("请输入评论内容");
                break;
            default:
                break;
        }
    }

    public int getSignInType() {
        return signInType;
    }

    private void initView() {
        dialogTitle_TextView = findViewById(R.id.tv_dialog_title);
        send_Button = findViewById(R.id.btn_dialog_send);
        cancle_TextView = findViewById(R.id.tv_dialog_cancle);
        cancle_TextView.setText("取消");
        opinion_EditText = findViewById(R.id.et_instruct_opinion);
        opinion_EditText.setVisibility(View.VISIBLE);
        cancle_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mike_TextView = findViewById(R.id.tv_mike);
        mike_TextView.setTypeface(iconFont);
        mike_TextView.setVisibility(View.VISIBLE);
        mike_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MikeEditTextUtil util = new MikeEditTextUtil(activity, opinion_EditText);
                util.viewShow();
            }
        });
    }

    /**
     * 确定键监听器
     */
    public void setOnPositiveListener(View.OnClickListener listener) {
        send_Button.setOnClickListener(listener);
    }


//    /**
//     * 取消键监听器
//     */
//    public void setOnNegativeListener(View.OnClickListener listener) {
//        cancle_TextView.setOnClickListener(listener);
//    }
}
