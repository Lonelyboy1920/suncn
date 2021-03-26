package com.suncn.ihold_zxztc.activity.application.meeting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GIEditText;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.BaseGlobal;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.MikeEditTextUtil;

import java.util.HashMap;

/**
 * 会议发言提交页面
 */
public class MeetSpeak_AddActivity extends BaseActivity {
    @BindView(id = R.id.et_title)
    private GIEditText title_EditText;//信息标题
    @BindView(id = R.id.tv_mike, click = true)
    private TextView mike_TextView; // 语音识别按钮
    @BindView(id = R.id.et_content)
    private GIEditText content_EditText;//信息内容
    private String title;
    private String content;
    private String headTitle; // 标题名称
    private String strMeetSpeakNoticeId;

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_meetspeak_add);
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
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strMeetSpeakNoticeId = bundle.getString("strMeetSpeakNoticeId");
            headTitle = bundle.getString("headTitle");
            setHeadTitle(headTitle);
        }
        goto_Button.setVisibility(View.VISIBLE);
        goto_Button.setText(getString(R.string.string_submit));
        goto_Button.refreshFontType(activity, "2");
        goto_Button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_mike:
                MikeEditTextUtil util = new MikeEditTextUtil(activity, content_EditText);
                util.viewShow();
                break;
            case R.id.btn_goto:
                title = title_EditText.getText().toString().trim();
                content = content_EditText.getText().toString().trim();
                if (GIStringUtil.isBlank(title)) {
                    showToast(getString(R.string.string_please_enter_title));
                    title_EditText.requestFocus();
                } else if (GIStringUtil.isBlank(content)) {
                    showToast(getString(R.string.string_please_enter_the_text));
                    content_EditText.requestFocus();
                } else {
                    submitMeetSpeanInfo();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        title_EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.contains("\n")) {
                    text = text.replace("\n", "");
                    title_EditText.setText(text);
                }
            }
        });
    }

    /**
     * 提交会议发言
     */
    private void submitMeetSpeanInfo() {
        prgDialog.showSubmitDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strMeetSpeakNoticeId", strMeetSpeakNoticeId);
        textParamMap.put("strTitle", title);
        textParamMap.put("strContent", content);
        doRequestNormal(ApiManager.getInstance().submitMeetSpeakInfo(textParamMap), 0);
    }

    /**
     * 请求结果
     */
    private void doLogic(Object obj, int what) {
        prgDialog.closePrgDialog();
        String totastMessage = null;
        switch (what) {
            case 0:
                prgDialog.closePrgDialog();
                try {
                    totastMessage = "提交成功！";
                    setResult(RESULT_OK);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    totastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }
        if (totastMessage != null) {
            showToast(totastMessage);
        }
    }
}
