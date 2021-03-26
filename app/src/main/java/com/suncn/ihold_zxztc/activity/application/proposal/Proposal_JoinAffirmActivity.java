package com.suncn.ihold_zxztc.activity.application.proposal;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.MikeEditTextUtil;

import java.util.HashMap;

/**
 * 联名提案详情联名确认界面
 */
public class Proposal_JoinAffirmActivity extends BaseActivity {
    @BindView(id = R.id.ll_join)
    private LinearLayout join_LinearLayout;//是否联名
    @BindView(id = R.id.rb_yes)
    private RadioButton yes_RadioButton;//同意联名RadioButton
    @BindView(id = R.id.et_idea)
    private GIEditText idea_EditText;//联名意见EditText
    @BindView(id = R.id.tv_mike, click = true)
    private TextView mike_TextView; // 语音识别按钮
    private String strId; // 提案ID
    private String strIdea;
    private String strAttend;
    private boolean isFromRemind;
    private int sign;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_proposal_joinaffirm);
    }

    @Override
    public void initData() {
        super.initData();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };

        idea_EditText.setTextView(findViewById(R.id.tv_count));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isFromRemind = bundle.getBoolean("isFromRemind", false);
            strAttend = bundle.getString("strAttend");
            strId = bundle.getString("strId");
            sign = bundle.getInt("sign", DefineUtil.zxta_lmta);
            goto_Button.setText(getString(R.string.string_submit));
            goto_Button.setVisibility(View.VISIBLE);
            goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            goto_Button.refreshFontType(activity, "2");
            if ("-1".equals(strAttend)) {
                join_LinearLayout.setVisibility(View.GONE);
                setHeadTitle(getString(R.string.string_confirmation_by_secondment));
                idea_EditText.setHint(R.string.string_please_enter_second_comments);
            } else {
                setHeadTitle(getString(R.string.string_joint_confirm));
                join_LinearLayout.setVisibility(View.VISIBLE);
                idea_EditText.setHint(R.string.string_please_enter_joint_comments);

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goto:
                strIdea = idea_EditText.getText().toString().trim();
                if (GIStringUtil.isBlank(strIdea)) {
                    showToast(idea_EditText.getHint().toString());
                    idea_EditText.requestFocus();
                } else {
                    doReportReply();
                }
                break;
            case R.id.tv_mike:
                MikeEditTextUtil util = new MikeEditTextUtil(activity, idea_EditText);
                util.viewShow();
                break;
            default:
                break;
        }
        super.onClick(v);
    }

    /**
     * 联名确认/附议确认
     */
    private void doReportReply() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId); // 联名id
        textParamMap.put("strReason", strIdea); // 委员意见
        if (join_LinearLayout.getVisibility() == View.VISIBLE) {
            if (yes_RadioButton.isChecked())
                textParamMap.put("intAttend", "1"); // 是否同意提名（1-同意联名；0-不同意联名； 2-待确认）
            else
                textParamMap.put("intAttend", "0"); // 是否同意提名（1-同意联名；0-不同意联名； 2-待确认）
            if (sign == DefineUtil.sqmy) {
                doRequestNormal(ApiManager.getInstance().InfoAllySureServlet(textParamMap), 0);
            } else {
                doRequestNormal(ApiManager.getInstance().dealAllySureServlet(textParamMap), 0);
            }
        } else {
            doRequestNormal(ApiManager.getInstance().dealSupport(textParamMap), 0);
        }
    }

    /**
     * 请求结果
     */
    private void doLogic(Object obj, int what) {
        String toastMessage = null;
        switch (what) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    BaseGlobal baseGlobal = (BaseGlobal) obj;
                   /* if ("-1".equals(strAttend)) {
                        toastMessage = "附议确认操作成功！";
                    } else {
                        toastMessage = "联名确认操作成功！";
                    }*/
                    if (isFromRemind) {
                        setResult(-2);
                    } else {
                        setResult(RESULT_OK);
                    }
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }

        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }
}
