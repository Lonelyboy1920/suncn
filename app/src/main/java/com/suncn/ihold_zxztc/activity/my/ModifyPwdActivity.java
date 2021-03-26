package com.suncn.ihold_zxztc.activity.my;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gavin.giframe.authcode.GIAESOperator;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIUtil;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.view.ClearEditText;

import java.util.HashMap;

/**
 * 修改密码
 */
public class ModifyPwdActivity extends BaseActivity {
    @BindView(id = R.id.et_old_pwd)
    private ClearEditText oldPwd_EditText; // 旧密码
    @BindView(id = R.id.tv_old_pwd, click = true)
    private TextView oldPwd_TextView;//显示旧密码
    @BindView(id = R.id.et_new_pwd)
    private ClearEditText newPwd_EditText; // 新密码
    @BindView(id = R.id.tv_new_pwd, click = true)
    private TextView newPwd_TextView;//显示新密码
    @BindView(id = R.id.et_confirm_pwd)
    private ClearEditText confirm_EditText; // 确认密码
    @BindView(id = R.id.tv_confirm_pwd, click = true)
    private TextView confirmPwd_TextView;//显示确认密码
    @BindView(id = R.id.ll_old_pwd)
    private LinearLayout oldPwd_LinearLayout; // 原密码布局
    @BindView(id = R.id.btn_submit, click = true)
    private Button submit_Button;

    private boolean isForget;//是否是从忘记密码界面过来的
    private boolean isOldPwdVisible;
    private boolean isNewPwdVisible;
    private boolean isconfirmPwdVisible;
    private String newPwd = "";
    private String strMobile = "";

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
            isForget = bundle.getBoolean("isForget", false);
        }
        if (isForget) {
            setHeadTitle(getString(R.string.string_recover_password));
            strMobile = GISharedPreUtil.getString(activity, "strMobile");
            oldPwd_LinearLayout.setVisibility(View.GONE);
        } else {
            setHeadTitle(getString(R.string.string_change_pwd));
        }
        isOldPwdVisible = GISharedPreUtil.getBoolean(activity, "isOldPwdVisible");
        if (isOldPwdVisible) {
            oldPwd_TextView.setText(R.string.fontion_pwd_visible);
            oldPwd_EditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            oldPwd_TextView.setText(R.string.fontion_pwd_gone);
            oldPwd_EditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        isNewPwdVisible = GISharedPreUtil.getBoolean(activity, "isNewPwdVisible");
        if (isNewPwdVisible) {
            newPwd_TextView.setText(R.string.fontion_pwd_visible);
            newPwd_EditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            newPwd_TextView.setText(R.string.fontion_pwd_gone);
            newPwd_EditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        isconfirmPwdVisible = GISharedPreUtil.getBoolean(activity, "isconfirmPwdVisible");
        if (isconfirmPwdVisible) {
            confirmPwd_TextView.setText(R.string.fontion_pwd_visible);
            confirm_EditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            confirmPwd_TextView.setText(R.string.fontion_pwd_gone);
            confirm_EditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        setContentView(R.layout.activity_modify_pwd);
    }

    /**
     * 请求结果
     */
    private void doLogic(Object object, int sign) {
        prgDialog.closePrgDialog();
        String toastMessage = null;
        switch (sign) {
            case 0:
                try {
                    if (!isForget) {
                        GISharedPreUtil.setValue(activity, "PWD", newPwd);
                    }
                    setResult(RESULT_OK);
                    activity.finish();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_old_pwd:
                if (isOldPwdVisible) { // 隐藏密码
                    isOldPwdVisible = false;
                    oldPwd_TextView.setText(R.string.fontion_pwd_gone);
                    oldPwd_EditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else { // 显示密码
                    isOldPwdVisible = true;
                    oldPwd_TextView.setText(R.string.fontion_pwd_visible);
                    oldPwd_EditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                GISharedPreUtil.setValue(activity, "isOldPwdVisible", isOldPwdVisible);
                break;
            case R.id.tv_new_pwd:
                if (isNewPwdVisible) { // 隐藏密码
                    isNewPwdVisible = false;
                    newPwd_TextView.setText(R.string.fontion_pwd_gone);
                    newPwd_EditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else { // 显示密码
                    isNewPwdVisible = true;
                    newPwd_TextView.setText(R.string.fontion_pwd_visible);
                    newPwd_EditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                GISharedPreUtil.setValue(activity, "isNewPwdVisible", isNewPwdVisible);
                break;
            case R.id.tv_confirm_pwd:
                if (isconfirmPwdVisible) { // 隐藏密码
                    isconfirmPwdVisible = false;
                    confirmPwd_TextView.setText(R.string.fontion_pwd_gone);
                    confirm_EditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else { // 显示密码
                    isconfirmPwdVisible = true;
                    confirmPwd_TextView.setText(R.string.fontion_pwd_visible);
                    confirm_EditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                GISharedPreUtil.setValue(activity, "isconfirmPwdVisible", isconfirmPwdVisible);
                break;
            case R.id.btn_submit:
                GIUtil.closeSoftInput(activity);
                checkInput();
                break;
            default:
                break;
        }
    }

    private void checkInput() {
        String oldPwd = oldPwd_EditText.getText().toString();
        newPwd = newPwd_EditText.getText().toString();
        String confirmPwd = confirm_EditText.getText().toString();
        if (!isForget && GIStringUtil.isBlank(oldPwd)) {
            oldPwd_EditText.requestFocus();
            showToast(getString(R.string.string_please_enter_the_original_password));
        } else if (GIStringUtil.isBlank(newPwd)) {
            newPwd_EditText.requestFocus();
            showToast(getString(R.string.string_please_enter_a_new_password));
        } else if (!GIStringUtil.isValidPassword(newPwd)) {
            newPwd_EditText.requestFocus();
            showToast(getString(R.string.string_please_enter_the_correct_password_format) + "(" + getString(R.string.string_modify_pwd_tips) + ") ！");
        } else if (GIStringUtil.isBlank(confirmPwd)) {
            confirm_EditText.requestFocus();
            showToast(getString(R.string.string_please_confirm_the_new_password));
        } else if (!confirmPwd.equals(newPwd)) {
            confirm_EditText.requestFocus();
            showToast(getString(R.string.string_passwords_entered_twice_are_inconsistent));
        } else {
            doChg(oldPwd, newPwd);
        }
    }

    /**
     * 修改密码
     */
    private void doChg(String oldPwd, String newPassword) {
        newPwd = newPassword;
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<String, String>();
        if (!isForget) { // 修改密码
            textParamMap.put("intType", "10");
            textParamMap.put("strOldPWD", GIAESOperator.getInstance().encrypt(oldPwd));
        } else { // 忘记密码
            textParamMap.put("intType", "11");
            textParamMap.put("strMobile", strMobile);
        }
        newPwd = GIAESOperator.getInstance().encrypt(newPwd);
        textParamMap.put("strPWD", newPwd);

        doRequestNormal(ApiManager.getInstance().modifyUsePwd(textParamMap), 0);
    }
}
