package com.suncn.ihold_zxztc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIToastUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.my.ModifyPwdActivity;
import com.suncn.ihold_zxztc.bean.SmsReceiveBean;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.utils.CountDownTimerUtils;

import java.util.HashMap;

/**
 * 忘记密码
 * Created by whh on 2018-7-2.
 */
public class ForgetPwdActivity extends BaseActivity {
    @BindView(id = R.id.btn_get_code, click = true)
    private TextView getCode_Button; // 获取验证码
    @BindView(id = R.id.et_tel)
    private EditText tel_EditText;//手机号码EditText
    @BindView(id = R.id.et_code)
    private EditText code_EditText;//验证码EditText
    @BindView(id = R.id.btn_next, click = true)
    private Button next_Button;//下一步Button
    private SmsReceiveBean smsReceiveBean;
    private boolean isGetCode = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    activity.finish();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_forget_pwd);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle("找回密码");
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
    }

    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        switch (sign) {
            case 1:
                isGetCode = false;
                Bundle bundle = new Bundle();
                bundle.putBoolean("isForget", true);
                bundle.putString("strMobile", smsReceiveBean.getStrMobile());
                showActivity(activity, ModifyPwdActivity.class, bundle, 0);
                break;
            case 0:
                prgDialog.closePrgDialog();
                isGetCode = true;
                try {
                    smsReceiveBean = (SmsReceiveBean) data;
                    CountDownTimerUtils countDownTimers = new CountDownTimerUtils(getCode_Button, 60000, 1000);
                    countDownTimers.start();
                    GISharedPreUtil.setValue(activity, "strMobile", smsReceiveBean.getStrMobile());
                    GISharedPreUtil.setValue(activity, "strMsgId", smsReceiveBean.getStrMsgId());
                    GISharedPreUtil.setValue(activity, "USER", smsReceiveBean.getStrUserId());
                } catch (Exception e) {
                    e.printStackTrace();
                    toastMessage = getString(R.string.data_error);
                }
                break;
            default:
                break;
        }
        if (GIStringUtil.isNotEmpty(toastMessage)) {
            showToast(toastMessage);
        }
    }


    @Override
    public void onClick(View v) {
        String tel = tel_EditText.getText().toString();
        switch (v.getId()) {
            case R.id.btn_get_code:
                if (GIStringUtil.isBlank(tel)) {
                    tel_EditText.requestFocus();
                    GIToastUtil.showMessage(activity, "请输入手机号码");
                } else if (!GIStringUtil.isMobile(tel)) {
                    tel_EditText.requestFocus();
                    GIToastUtil.showMessage(activity, "请输入正确格式的手机号码");
                } else {
                    getCode(tel);
                }
                break;
            case R.id.btn_next:
                String code = code_EditText.getText().toString();
                if (GIStringUtil.isBlank(tel)) {
                    tel_EditText.requestFocus();
                    GIToastUtil.showMessage(activity, "请输入手机号码");
                } else if (!GIStringUtil.isMobile(tel)) {
                    tel_EditText.requestFocus();
                    GIToastUtil.showMessage(activity, "请输入正确格式的手机号码");
                } else if (!isGetCode) {
                    showToast("请先获取验证码");
                } else if (GIStringUtil.isBlank(code)) {
                    showToast("请输入验证码");
                } else {
                    checkCode(code);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getCode(String tel) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strMobile", tel);
        doRequestNormal(ApiManager.getInstance().getSmsCode(textParamMap), 0);
    }

    /**
     * 校验验证码
     */
    private void checkCode(String strExNo) {
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strMsgId", smsReceiveBean.getStrMsgId());
        textParamMap.put("strExNo", strExNo);
        textParamMap.put("strUserId", smsReceiveBean.getStrUserId());
        doRequestNormal(ApiManager.getInstance().checkSmsCode(textParamMap), 1);
    }

}
