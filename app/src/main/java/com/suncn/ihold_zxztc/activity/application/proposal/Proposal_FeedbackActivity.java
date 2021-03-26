package com.suncn.ihold_zxztc.activity.application.proposal;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;

import java.util.HashMap;

/**
 * 提案详情意见反馈界面
 */
public class Proposal_FeedbackActivity extends BaseActivity {
    @BindView(id = R.id.rb_yes)
    private RadioButton yes_RadioButton;//办理态度满意RadioButton
    @BindView(id = R.id.rb_no)
    private RadioButton no_RadioButton;//办理态度不满意RadioButton
    @BindView(id = R.id.rb_ryes)
    private RadioButton ryes_RadioButton;//办理结果满意RadioButton
    @BindView(id = R.id.rb_rno)
    private RadioButton rno_RadioButton;//办理结果不满意RadioButton
    @BindView(id = R.id.ll_attitude)
    private LinearLayout attitude_Layout;//办理态度意见LinearLayout
    @BindView(id = R.id.et_attitude_idea)
    private GIEditText attitudeIdea_EditText;//办理态度意见EditText
    @BindView(id = R.id.ll_result)
    private LinearLayout result_Layout;//办理结果LinearLayout
    @BindView(id = R.id.et_result_idea)
    private GIEditText resultIdea_EditText;//办理结果EditText
    @BindView(id = R.id.et_idea)
    private GIEditText idea_EditText;//委员意见EditText
    @BindView(id = R.id.ll_idea_attitude)
    private LinearLayout llIdeaAttidude;
    @BindView(id = R.id.ll_idea)
    private LinearLayout llIdea;
    @BindView(id = R.id.ll_idea_lw)
    private LinearLayout llIdeaLW;
    @BindView(id = R.id.rb_yes_lw)
    private RadioButton yes_RadioButton_lw;
    @BindView(id = R.id.rb_no_lw)
    private RadioButton no_RadioButton_lw;
    @BindView(id = R.id.rb_middle_lw)
    private RadioButton middle_RadioButton_lw;
    @BindView(id = R.id.rg_lw)
    private RadioGroup rgLW;


    private String strId; // 提案ID
    private String strMemReturnAttitude;
    private String strMemReturnAttitudeTxt;
    private String strMemReturnIdea;
    private String strMemReturnTxt;
    private String strMemReturnMainTxt;
    private boolean isFromRemind;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_proposal_feedback);
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBar();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        setHeadTitle("委员反馈");
        findViewById(R.id.view_place).setVisibility(View.GONE);
        attitudeIdea_EditText.setTextView(findViewById(R.id.tv_attitude_idea_count));
        resultIdea_EditText.setTextView(findViewById(R.id.tv_result_idea_count));
        idea_EditText.setTextView(findViewById(R.id.tv_idea_count));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isFromRemind = bundle.getBoolean("isFromRemind", false);
            strId = bundle.getString("strId");

            goto_Button.setText("提交");
            goto_Button.refreshFontType(activity, "2");
            goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            goto_Button.setVisibility(View.VISIBLE);
            strMemReturnAttitude = "1";
            yes_RadioButton.setChecked(true);
            strMemReturnIdea = "1";
            ryes_RadioButton.setChecked(true);
            if (ProjectNameUtil.isLWQZX(activity)) {
                yes_RadioButton_lw.setChecked(true);
            }
            attitudeIdea_EditText.setText(bundle.getString("strMemReturnAttitudeTxt"));
            resultIdea_EditText.setText(bundle.getString("strMemReturnTxt"));
            idea_EditText.setText(bundle.getString("strMemReturnMainTxt"));
        }

        if (ProjectNameUtil.isLWQZX(activity)) {
            llIdea.setVisibility(View.GONE);
            llIdeaAttidude.setVisibility(View.GONE);
            llIdeaLW.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goto:
                if (!ProjectNameUtil.isLWQZX(activity)) {
                    if (yes_RadioButton.isChecked()) {
                        strMemReturnAttitude = "1";
                    } else {
                        strMemReturnAttitude = "3";
                        strMemReturnAttitudeTxt = attitudeIdea_EditText.getText().toString().trim();
                    }
                }
                if (ryes_RadioButton.isChecked()) {
                    strMemReturnIdea = "1";
                } else {
                    strMemReturnIdea = "3";
                    strMemReturnTxt = resultIdea_EditText.getText().toString().trim();
                }
                strMemReturnMainTxt = idea_EditText.getText().toString().trim();
                if (!yes_RadioButton.isChecked() && GIStringUtil.isBlank(strMemReturnAttitudeTxt)) {
                    showToast(attitudeIdea_EditText.getHint().toString());
                    attitudeIdea_EditText.requestFocus();
                } else if (!ryes_RadioButton.isChecked() && GIStringUtil.isBlank(strMemReturnTxt)) {
                    showToast(resultIdea_EditText.getHint().toString());
                    resultIdea_EditText.requestFocus();
                } else if (GIStringUtil.isBlank(strMemReturnMainTxt)) {
                    showToast(idea_EditText.getHint().toString());
                    idea_EditText.requestFocus();
                } else {
                    doReturn();
                }
                break;
            default:
                break;
        }
        super.onClick(v);
    }

    @Override
    public void setListener() {
        yes_RadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    attitude_Layout.setVisibility(View.GONE);
                } else {
                    attitude_Layout.setVisibility(View.VISIBLE);
                }
            }
        });
        rgLW.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == yes_RadioButton_lw.getId()) {
                    strMemReturnAttitude = "1";
                } else if (checkedId == no_RadioButton_lw.getId()) {
                    strMemReturnAttitude = "3";
                } else {
                    strMemReturnAttitude = "2";
                }
            }
        });
        ryes_RadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    result_Layout.setVisibility(View.GONE);
                } else {
                    result_Layout.setVisibility(View.VISIBLE);
                }
            }
        });
        super.setListener();
    }

    /**
     * 委员反馈（已报提案详情意见反馈）
     */
    private void doReturn() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId); // 提案ID
        if (ProjectNameUtil.isLWQZX(activity)) {
            textParamMap.put("strReturnState", strMemReturnAttitude);
            textParamMap.put("strReturnTxt", strMemReturnMainTxt); // 具体意见
        } else {
            textParamMap.put("strMemReturnAttitude", strMemReturnAttitude); // 办理态度满意度(1、满意，3、不满意）
            if (!yes_RadioButton.isChecked())
                textParamMap.put("strMemReturnAttitudeTxt", strMemReturnAttitudeTxt); // 办理态度意见(不满意时必输）
            textParamMap.put("strMemReturnIdea", strMemReturnIdea); // 办理结果满意度（1、满意，3、不满意）
            if (!ryes_RadioButton.isChecked())
                textParamMap.put("strMemReturnTxt", strMemReturnTxt); // 办理结果意见(不满意时输入）
            textParamMap.put("strMemReturnMainTxt", strMemReturnMainTxt); // 具体意见
        }

        doRequestNormal(ApiManager.getInstance().dealProposalFeedBack(textParamMap), 0);
    }

    /**
     * 请求结果
     */
    private void doLogic(int what, Object obj) {
        String toastMessage = null;
        switch (what) {
            case 0:
                prgDialog.closePrgDialog();
                try {
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
