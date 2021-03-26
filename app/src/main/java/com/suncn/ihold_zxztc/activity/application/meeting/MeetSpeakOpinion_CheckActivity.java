package com.suncn.ihold_zxztc.activity.application.meeting;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.utils.MikeEditTextUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;

import java.util.HashMap;

import skin.support.content.res.SkinCompatResources;

/**
 * 会议发言、社情民意审核
 */
public class MeetSpeakOpinion_CheckActivity extends BaseActivity {
    @BindView(id = R.id.rg_result)
    private RadioGroup result_RadioGroup;//审核结果RadioGroup
    @BindView(id = R.id.rb_used)
    private RadioButton used_RadioButton;//采用RadioButton
    @BindView(id = R.id.rb_unused)
    private RadioButton unused_RadioButton;//未采用RadioButton
    @BindView(id = R.id.tv_speak_type)
    private TextView speakType_TextView;//发言类型
    @BindView(id = R.id.ll_speak_type, click = true)
    private LinearLayout speakType_LinearLayout;
    @BindView(id = R.id.et_idea)
    private GIEditText etIdea;
    @BindView(id = R.id.ll_idea)
    private LinearLayout llIdea;
    @BindView(id = R.id.tv_analysis_count)
    TextView analysisCount_TextView;
    @BindView(id = R.id.rb_reback)
    private RadioButton rbReback;
    @BindView(id = R.id.llCommitToZx)
    private LinearLayout llCommitToZx;
    @BindView(id = R.id.llCommitToMsz)
    private LinearLayout llCommitToMsz;
    @BindView(id = R.id.rgCommitToZx)
    private RadioGroup rgCommitToZx;
    @BindView(id = R.id.rgCommitToMsz)
    private RadioGroup rgCommitToMsz;
    @BindView(id = R.id.rbCommitToZxNo)
    private RadioButton rbCommitToZxNo;
    @BindView(id = R.id.rbCommitToZxYes)
    private RadioButton rbCommitToZxYes;
    @BindView(id = R.id.rbCommitToMszYes)
    private RadioButton rbCommitToMszYes;
    @BindView(id = R.id.rbCommitToMszNo)
    private RadioButton rbCommitToMszNo;
    @BindView(id = R.id.tv_mike, click = true)
    private TextView mike_TextView; // 语音识别按钮
    private String strCheckAgreeState = "0"; // 会议发言审核状态：0(采用),1(不采用)
    private String strType; // 当strCheckAgreeState为采用的时候，才有发言类型：01(口头发言),02(书面发言)
    private String strId;
    private boolean isSocialOpinions; // 是否为社情民意信息
    private String strExamineStatus = "1"; // 社情民意审核状态:1通过、0不通过
    private String mIntToMsz;  //提交给秘书张  0不显示 1显示  滁州是政协添加
    private String mIntToZx;  //提交给主席  0不显示 1显示  滁州市政协添加
    private String intToMsz;
    private String intToZx;
    private String mStrType;
    private String mIntPass;  //intPass  0-隐藏不通过按钮  1-展示不通过按钮

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_meetspeak_check);
        isShowBackBtn = true;
    }

    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            strId = bundle.getString("strId");
            isSocialOpinions = bundle.getBoolean("isSocialOpinions");

            if (ProjectNameUtil.isCZSZX(activity)) {
                mIntToMsz = bundle.getString("intToMsz");
                mIntToZx = bundle.getString("intToZx");
                mStrType = bundle.getString("strTypeHYFY");
                mIntPass = bundle.getString("intPass", "0");
            }
        }
        if (isSocialOpinions) {
            setHeadTitle("社情民意审核");
            used_RadioButton.setText("通过");
            unused_RadioButton.setText("不通过");
        } else {
            setHeadTitle("发言审核");
        }

        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(sign, data);
            }
        };
        goto_Button.setText("确定");
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        goto_Button.refreshFontType(activity, "2");
        goto_Button.setVisibility(View.VISIBLE);
        llIdea.setVisibility(View.VISIBLE);
        etIdea.setTextView(analysisCount_TextView);

        if (ProjectNameUtil.isCZSZX(activity)) {   //滁州市政协，增加退回选项
            rbReback.setVisibility(View.VISIBLE);
            speakType_TextView.setText(mStrType == null ? "" : mStrType.equals("01") ? "口头发言" : "书面发言"); //01-口头发言 02-书面发言
            if (isSocialOpinions) {
                if ("0".equals(mIntPass)) {
                    unused_RadioButton.setVisibility(View.GONE);
                } else {
                    unused_RadioButton.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        result_RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_unused) {
                    speakType_LinearLayout.setVisibility(View.GONE);
                    strExamineStatus = "0";
                    strCheckAgreeState = "1";
                    llCommitToMsz.setVisibility(View.GONE);
                    llCommitToZx.setVisibility(View.GONE);
                } else if (checkedId == R.id.rb_used) {
                    strExamineStatus = "1";
                    strCheckAgreeState = "0";
                    if (!isSocialOpinions) {
                        speakType_LinearLayout.setVisibility(View.VISIBLE);
                    }

                    if (ProjectNameUtil.isCZSZX(activity)) {
                        if ("1".equals(mIntToMsz)) {
                            llCommitToMsz.setVisibility(View.VISIBLE);
                        }
                        if ("1".equals(mIntToZx)) {
                            llCommitToZx.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (checkedId == R.id.rb_reback) {
                    strExamineStatus = "-1";
                    strCheckAgreeState = "-1";
                    llCommitToMsz.setVisibility(View.GONE);
                    llCommitToZx.setVisibility(View.GONE);
                    speakType_LinearLayout.setVisibility(View.GONE);
                }
            }
        });

        rgCommitToMsz.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.rbCommitToMszNo) {
                intToMsz = "0";
            } else if (i == R.id.rbCommitToMszYes) {
                intToMsz = "1";
            }
        });

        rgCommitToZx.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.rbCommitToZxNo) {
                intToZx = "0";
            } else if (i == R.id.rbCommitToZxYes) {
                intToZx = "1";
            }
        });
    }

    /**
     * 检查数据
     */
    private void checkData() {
        String speakType = speakType_TextView.getText().toString();
        int checkedRadioButtonId = result_RadioGroup.getCheckedRadioButtonId();
        if (-1 == checkedRadioButtonId) {
            showToast("请选择审核结果");
            return;
        }
        if ("0".equals(strCheckAgreeState) && !isSocialOpinions) {
            if (GIStringUtil.isBlank(speakType)) {
                showToast("请选择发言类型");
                return;
            }
        }
        if (GIStringUtil.isBlank(etIdea.getText().toString())) {
            showToast(etIdea.getHint().toString());
            return;
        }
        sendCheckResult();
    }

    /**
     * 发送审核结果
     */
    private void sendCheckResult() {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strId);
        if (isSocialOpinions) {
            textParamMap.put("strExamineStatus", strExamineStatus);
            textParamMap.put("strContent", etIdea.getText().toString());
            doRequestNormal(ApiManager.getInstance().checkOpinions(textParamMap), 0);
        } else {
            textParamMap.put("strContent", etIdea.getText().toString());
            textParamMap.put("strCheckAgreeState", strCheckAgreeState);
            if (GIStringUtil.isNotBlank(strType)) {
                textParamMap.put("strType", strType);
            }
            if (ProjectNameUtil.isCZSZX(activity)) { //滁州市政协增加是否提交给秘书长或者主席
                if (llCommitToMsz.isShown()) {
                    textParamMap.put("intToMsz", intToMsz);
                }
                if (llCommitToZx.isShown()) {
                    textParamMap.put("intToZx", intToZx);
                }
            }
            doRequestNormal(ApiManager.getInstance().checkMeetSpeak(textParamMap), 0);
        }
    }

    private void doLogic(int sign, Object object) {
        prgDialog.closePrgDialog();
        showToast("审核成功");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        MikeEditTextUtil util;
        switch (v.getId()) {
            case R.id.btn_goto:
                checkData();
                break;
            case R.id.ll_speak_type:
                showChooseDialog();
                break;
            case R.id.tv_mike:
                util = new MikeEditTextUtil(activity, etIdea);
                util.viewShow();
                break;
        }
    }

    private void showChooseDialog() {
        final String[] menuArray = {"口头发言", "书面发言"};
        final NormalListDialog normalListDialog = new NormalListDialog(activity, menuArray);
        normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                speakType_TextView.setText(menuArray[position]);
                if (0 == position) {
                    strType = "01";
                } else {
                    strType = "02";
                }
                normalListDialog.dismiss();
            }
        });
        normalListDialog.titleBgColor(SkinCompatResources.getColor(activity,R.color.view_head_bg));
        normalListDialog.titleTextColor(Color.WHITE);
        normalListDialog.show();
    }
}
