package com.suncn.ihold_zxztc.activity.application.meeting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.IosAttendBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.view.dialog.RemindHandleDialog;

import java.util.HashMap;

import static com.suncn.ihold_zxztc.R.id.btn_submit;
import static com.suncn.ihold_zxztc.R.id.tv_address;

/**
 * 签到结果界面
 */
public class SignInResultActivity extends BaseActivity {
    @BindView(id = R.id.tv_result)
    private TextView result_TextView;//签到结果TextView
    @BindView(id = R.id.tv_time)
    private TextView time_TextView;//签到时间TextView
    @BindView(id = tv_address)
    private TextView address_TextView;
    @BindView(id = btn_submit, click = true)
    private Button submit_Button;

    private String address; // 签到地址
    private int intQdAttend; // 迟到签到则返回，值为-1
    private String strMtId; // 会议或活动的id
    private String strAttendId; // 会议或活动记录的id

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_singin_result);
        isShowBackBtn = true;
    }

    @Override
    public void initData() {
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            setHeadTitle(bundle.getString("headTitle") + getString(R.string.string_result));
            time_TextView.setText(bundle.getString("strQdTime"));
            address = bundle.getString("address");
            strMtId = bundle.getString("strMtId");
            strAttendId = bundle.getString("strAttendId");
            intQdAttend = bundle.getInt("intQdAttend");
            if (GIStringUtil.isBlank(address)) {
                address_TextView.setVisibility(View.INVISIBLE);
            } else {
                address_TextView.setVisibility(View.VISIBLE);
                address_TextView.setText("\ue686 " + address);
            }
            if (intQdAttend == 1) {
                result_TextView.setText("\ue687 "+getString(R.string.string_check_in_late));
                result_TextView.setTextColor(getResources().getColor(R.color.view_head_bg));
                submit_Button.setVisibility(View.VISIBLE);
            } else {
                result_TextView.setText("\ue685 "+getString(R.string.string_sign_in_successfully));
                result_TextView.setTextColor(getResources().getColor(R.color.zxta_state_green));
                submit_Button.setVisibility(View.GONE);
            }
        }
        goto_Button.setText(R.string.string_finish);
        goto_Button.setVisibility(View.VISIBLE);
        setResult(RESULT_OK);
        super.initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goto:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.btn_submit:
                showRemindDialog();
                break;
            default:
                break;
        }
        super.onClick(v);
    }

    /**
     * 报告迟到原因对话框
     */
    private void showRemindDialog() {
        final RemindHandleDialog remindHandleDialog = new RemindHandleDialog(activity);
        final EditText editText = remindHandleDialog.getContent_EditText();
        remindHandleDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idea = editText.getText().toString();
                if (GIStringUtil.isBlank(idea)) {
                    showToast(getString(R.string.string_please_enter_reason_for_late));
                } else {
                    doIosAttend(idea);
                    remindHandleDialog.dismiss();
                    GIUtil.closeSoftInput(activity);
                }
            }
        });
        remindHandleDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindHandleDialog.dismiss();
                GIUtil.closeSoftInput(activity);
            }
        });
        remindHandleDialog.show();
    }

    /**
     * 签到
     */
    private void doIosAttend(String strLaterReason) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strId", strMtId); // 会议或活动的id
        textParamMap.put("strLaterReason", strLaterReason); // 迟到原因
        textParamMap.put("strAttendId", strAttendId); // 出席记录的id
        doRequestNormal(ApiManager.getInstance().dealSign(textParamMap), 0);
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
                    IosAttendBean iosAttendBean = (IosAttendBean) obj;
                    showToast(getString(R.string.string_reason_for_late_report_successful));
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
