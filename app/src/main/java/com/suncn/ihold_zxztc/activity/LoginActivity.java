package com.suncn.ihold_zxztc.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.appcompat.app.AlertDialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.animation.SlideEnter.SlideTopEnter;
import com.flyco.dialog.widget.popup.base.BasePopup;
import com.gavin.giframe.authcode.GIAESOperator;
import com.gavin.giframe.ui.BindView;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIPhoneUtils;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.gavin.giframe.widget.RoundImageView;
import com.longchat.base.QDClient;
import com.longchat.base.callback.QDLoginOutCallBack;
import com.longchat.base.manager.listener.QDLoginOutCallBackManager;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.EventBusCarrier;
import com.suncn.ihold_zxztc.bean.LoginBean;
import com.suncn.ihold_zxztc.bean.SmsReceiveBean;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.CountDownTimerUtils;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.FingerprintUtil;
import com.suncn.ihold_zxztc.utils.LoginCarrier;
import com.suncn.ihold_zxztc.utils.LoginInterceptor;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.dialog.Login_SettingDialog;
import com.suncn.ihold_zxztc.view.ClearEditText;
import com.umeng.message.UTrack;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

/**
 * ????????????
 */
public class LoginActivity extends BaseActivity implements QDLoginOutCallBack {
    @BindView(id = R.id.img_head)
    private ImageView head_ImageView;
    @BindView(id = R.id.tv_debug, click = true)
    private TextView debug_TextView; // DEBUG??????????????????????????????
    @BindView(id = R.id.tv_back, click = true)
    private GITextView back_TextView;//?????????????????????
    @BindView(id = R.id.et_username)
    private ClearEditText username_EditText; // ?????????EditText
    @BindView(id = R.id.et_password)
    private ClearEditText password_EditText; // ??????EditText
    @BindView(id = R.id.et_code)
    private ClearEditText code_EditText; // ????????????EditText
    @BindView(id = R.id.tv_getcode, click = true)
    private TextView getcode_TextView;
    @BindView(id = R.id.cb_remember)
    private CheckBox remember_CheckBox; // ????????????CheckBox
    @BindView(id = R.id.tv_pwd_visual, click = true)
    private TextView pwdVisual_TextView;//??????????????????
    @BindView(id = R.id.tv_forget_pwd, click = true)
    private TextView forget_pwd_TextView;
    @BindView(id = R.id.tv_setting, click = true)
    private TextView setting_TextView; // ??????TextView
    @BindView(id = R.id.tv_finger_icon)
    private TextView finger_icon_TextView;
    @BindView(id = R.id.tv_user)
    private TextView user_TextView;
    @BindView(id = R.id.btn_finger_userlogin, click = true)
    private Button finger_userlogin_Button;//?????????????????????????????????
    @BindView(id = R.id.btn_finger_smslogin, click = true)
    private Button finger_smslogin_Button;//????????????????????????????????????
    @BindView(id = R.id.ll_finger_login)
    private LinearLayout finger_login_LinearLayout;//??????????????????LinearLayout
    @BindView(id = R.id.ll_normal_login)
    private LinearLayout normal_login_LinearLayout;//???????????????????????????
    @BindView(id = R.id.ll_pwd)
    private LinearLayout pwd_LinearLayout;//????????????LinearLayout
    @BindView(id = R.id.ll_code)
    private LinearLayout code_LinearLayout;//???????????????LinearLayout
    @BindView(id = R.id.ll_repwd)
    private LinearLayout repwd_LinearLayout;//??????????????????LinearLayout

    @BindView(id = R.id.iv_login, click = true)
    private ImageView login_Button;//??????????????????
    @BindView(id = R.id.btn_smslogin, click = true)
    private Button smsLogin_Button;//??????????????????
    @BindView(id = R.id.iv_finger_login, click = true)
    private ImageView finger_login_ImageView;//????????????
    @BindView(id = R.id.tvLogin, click = true)
    private TextView tvLogin;

    private AlertDialog fingerprintDialog;//???????????????dialog

    private String deviceCode; // ?????????
    private String username; // ?????????
    private String password; // ??????
    private boolean isVisible; // ??????????????????
    private int myClickCount; // ????????????????????????
    private String server; // OA ip
    private String port; // OA??????
    private int currentIndex = 2;
    private SimpleCustomPop mQuickCustomPopup;
    private boolean isChangeRole = false;
    private boolean isNeedExit; // ????????????????????????????????????APP?????????
    private boolean isReLogin; // ???????????????????????????

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // ????????????????????????????????????????????????
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !isNeedExit) {
            doBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void initData() {
        super.initData();
        mQuickCustomPopup = new SimpleCustomPop(activity);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentIndex = bundle.getInt("currentIndex", 2);
            isNeedExit = bundle.getBoolean("isNeedExit");
            isReLogin = bundle.getBoolean("isReLogin");
        }

        if (!getResources().getBoolean(R.bool.IS_OPEN_VISITOR)) {
            isNeedExit = true;
        }

        if (isNeedExit) { // ????????????????????????????????????APP?????????
            setCanExitApp(true);
            back_TextView.setVisibility(View.INVISIBLE);
        } else {
            setCanExitApp(false);
        }
        QDClient.getInstance().setLogOut(true);
        QDLoginOutCallBackManager.getInstance().addCallBack(this);
        GISharedPreUtil.setValue(activity, GISharedPreUtil.getString(activity, "strLoginUserId") + "isHstLogin", false);
        login_Button.setEnabled(false);
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        server = GISharedPreUtil.getString(activity, "server");
        port = GISharedPreUtil.getString(activity, "port");
        user_TextView.setText(GISharedPreUtil.getString(activity, "strName"));
        deviceCode = GISharedPreUtil.getString(activity, "deviceCode");
        username = GISharedPreUtil.getString(activity, "USER");
        password = GIAESOperator.getInstance().decrypt(GISharedPreUtil.getString(activity, "PWD"));
        username_EditText.setText(username);

        if (GISharedPreUtil.getBoolean(activity, "isOpenFingerLogin_" + GISharedPreUtil.getString(activity, "strUserId"))) {// ???????????????????????????????????????
            finger_login_LinearLayout.setVisibility(View.VISIBLE);
            normal_login_LinearLayout.setVisibility(View.GONE);
            initFingerprint(1);
        } else {
            finger_login_LinearLayout.setVisibility(View.GONE);
            normal_login_LinearLayout.setVisibility(View.VISIBLE);
            //initFingerprint(0);
        }
        if (GISharedPreUtil.getBoolean(activity, "RMBPWD")) {
            remember_CheckBox.setChecked(true);
            password_EditText.setText(password);
            login_Button.setEnabled(true);
        } else {
            remember_CheckBox.setChecked(false);
            password_EditText.setText("");
        }
        isVisible = GISharedPreUtil.getBoolean(activity, "isVisible");
        if (isVisible) {
            pwdVisual_TextView.setText(R.string.fontion_pwd_visible);
            password_EditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            pwdVisual_TextView.setText(R.string.fontion_pwd_gone);
            password_EditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        isDebugMode();
        if (ProjectNameUtil.isGZSZX(activity)) {
            code_LinearLayout.setVisibility(View.VISIBLE);
            finger_login_LinearLayout.setVisibility(View.GONE);
            normal_login_LinearLayout.setVisibility(View.VISIBLE);
            //repwd_LinearLayout.setVisibility(View.GONE);
            username_EditText.setText(GISharedPreUtil.getString(activity, "strMobile"));
            username_EditText.setHint("??????????????????");
            pwd_LinearLayout.setVisibility(View.GONE);
            repwd_LinearLayout.setVisibility(View.INVISIBLE);
            smsLogin_Button.setText("??????????????????");
        }
        if (!getResources().getBoolean(R.bool.IS_SHOW_CIRCLE_LOGINBTN)) {
            tvLogin.setVisibility(View.VISIBLE);
            login_Button.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                doBack();
                break;
            case R.id.tv_getcode: // ???????????????
                String mobile = username_EditText.getText().toString().trim();
                if (GIStringUtil.isBlank(mobile)) {
                    username_EditText.requestFocus();
                    showToast("?????????????????????");
                } else if (!GIStringUtil.isMobile(mobile)) {//????????????????????????????????????
                    showToast("??????????????????????????????");
                } else {
                    getSmsInfo(mobile);
                }
                break;
            case R.id.tv_pwd_visual: // ??????????????????
                if (isVisible) { // ????????????
                    isVisible = false;
                    pwdVisual_TextView.setText(R.string.fontion_pwd_gone);
                    password_EditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else { // ????????????
                    isVisible = true;
                    pwdVisual_TextView.setText(R.string.fontion_pwd_visible);
                    password_EditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                GISharedPreUtil.setValue(activity, "isVisible", isVisible);
                break;
            case R.id.tv_setting: // ????????????????????????
                Login_SettingDialog instructDialog = new Login_SettingDialog(activity);
                instructDialog.show();
                break;
            case R.id.btn_finger_userlogin: // ???????????????????????????
                finger_login_LinearLayout.setVisibility(View.GONE);
                normal_login_LinearLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_finger_smslogin: // ???????????????????????????
                finger_login_LinearLayout.setVisibility(View.GONE);
                normal_login_LinearLayout.setVisibility(View.VISIBLE);
                pwd_LinearLayout.setVisibility(View.GONE);
                code_LinearLayout.setVisibility(View.VISIBLE);
                repwd_LinearLayout.setVisibility(View.GONE);
                break;
            case R.id.tvLogin:
            case R.id.iv_login: // ??????
                if (code_LinearLayout.getVisibility() == View.VISIBLE) {
                    username = username_EditText.getText().toString();
                    if (GIStringUtil.isBlank(username)) {
                        username_EditText.requestFocus();
                        showToast("?????????????????????");
                    } else if (GIStringUtil.isBlank(code_EditText.getText().toString().trim())) {
                        code_EditText.requestFocus();
                        showToast("?????????????????????");
                    } else {
                        doLogin();
                    }
                } else {
                    username = username_EditText.getText().toString();
                    password = password_EditText.getText().toString();
                    if (GIStringUtil.isBlank(username)) {
                        username_EditText.requestFocus();
                        showToast("?????????????????????");
                    } else if (GIStringUtil.isBlank(password)) {
                        password_EditText.requestFocus();
                        showToast("??????????????????");
                    } else {
                        doLogin();
                    }
                }
                break;
            case R.id.btn_smslogin: // ??????????????????/????????????????????????
                String btnText = smsLogin_Button.getText().toString().trim();
                if (btnText.equals("??????????????????")) {
                    code_LinearLayout.setVisibility(View.VISIBLE);
                    finger_login_LinearLayout.setVisibility(View.GONE);
                    normal_login_LinearLayout.setVisibility(View.VISIBLE);
                    //repwd_LinearLayout.setVisibility(View.GONE);
                    username_EditText.setText(GISharedPreUtil.getString(activity, "strMobile"));
                    username_EditText.setHint("??????????????????");
                    pwd_LinearLayout.setVisibility(View.GONE);
                    repwd_LinearLayout.setVisibility(View.INVISIBLE);
                    smsLogin_Button.setText("??????????????????");
                }
                if (btnText.equals("??????????????????")) {
                    code_LinearLayout.setVisibility(View.GONE);
                    finger_login_LinearLayout.setVisibility(View.GONE);
                    normal_login_LinearLayout.setVisibility(View.VISIBLE);
                    username_EditText.setText(GISharedPreUtil.getString(activity, "USER"));
                    username_EditText.setHint("??????????????????/?????????");
                    pwd_LinearLayout.setVisibility(View.VISIBLE);
                    repwd_LinearLayout.setVisibility(View.VISIBLE);
                    smsLogin_Button.setText("??????????????????");
                }
                break;
            case R.id.iv_finger_login: // ??????????????????
                initFingerprint(1);
                break;
            case R.id.tv_forget_pwd:
                showActivity(activity, ForgetPwdActivity.class);
                break;
            case R.id.tv_debug: // ????????????????????????
                if (!DefineUtil.IS_OPEN_DEBUG) { // ?????????DEBUG????????????????????????????????????
                    myClickCount++;
                    if (myClickCount == 5) { // ??????5???
                        if (GISharedPreUtil.getBoolean(activity, "isDebugMode")) {
                            showToast("BEBUG??????????????????");
                            setting_TextView.setVisibility(View.GONE);
                            Utils.switchDebugMode(this, false);// DEBUG????????????
                        } else {
                            showToast("BEBUG??????????????????");
                            setting_TextView.setVisibility(View.VISIBLE);
                            Utils.switchDebugMode(this, true);// DEBUG????????????
                        }
                        myClickCount = 0;
                    }
                }
                break;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        username_EditText.addTextChangedListener(textWatcher);
        password_EditText.addTextChangedListener(textWatcher);
        code_EditText.addTextChangedListener(textWatcher);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (code_LinearLayout.getVisibility() == View.VISIBLE) {
                if (GIStringUtil.isBlank(username_EditText.getText().toString()) || GIStringUtil.isBlank(code_EditText.getText().toString())) {
                    login_Button.setEnabled(false);
                } else {
                    login_Button.setEnabled(true);
                }
            } else {
                if (GIStringUtil.isBlank(username_EditText.getText().toString()) || GIStringUtil.isBlank(password_EditText.getText().toString())) {
                    login_Button.setEnabled(false);
                } else {
                    login_Button.setEnabled(true);
                }
            }
        }
    };

    /**
     * ??????????????????
     */
    private void doBack() {
        if (isReLogin) {
            password = null;
            code_LinearLayout.setVisibility(View.GONE);
            doLogin();
        } else {
            Bundle bundle = new Bundle();
            LoginCarrier invoker = getIntent().getParcelableExtra(LoginInterceptor.mINVOKER);
            if (invoker != null && invoker.mbundle != null) {
                handler.sendEmptyMessage(0);
            } else {
                bundle.putInt("currentIndex", 2);
                skipActivity(activity, MainActivity.class, bundle);
            }
        }
    }

    /**
     * ???????????????DEBUG??????
     */
    private void isDebugMode() {
        if (!DefineUtil.IS_OPEN_DEBUG) { // ?????????DEBUG????????????????????????????????????
            if (GISharedPreUtil.getBoolean(activity, "isDebugMode")) {
                setting_TextView.setVisibility(View.VISIBLE);
            } else {
                setting_TextView.setVisibility(View.GONE);
            }
        } else {
            setting_TextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ????????????
     **/
    private void doLogin() {
        ApiManager.refreshRetrofit();
        HiPermission.create(activity).checkSinglePermission(Manifest.permission.READ_PHONE_STATE, new PermissionCallback() {
            @Override
            public void onGuarantee(String permisson, int position) { // ??????/?????????
                prgDialog.showLoadDialog();
                textParamMap = new HashMap<>();
                textParamMap.put("intType", "20");
                if (code_LinearLayout.getVisibility() == View.VISIBLE) { // ???????????????????????????
                    textParamMap.put("strMobile", username);
                    textParamMap.put("strMsgId", GISharedPreUtil.getString(activity, "strMsgId"));
                    textParamMap.put("strExNo", code_EditText.getText().toString().trim());//?????????
                } else if (finger_login_LinearLayout.getVisibility() == View.VISIBLE) { // ????????????????????????
                    textParamMap.put("strUsername", GISharedPreUtil.getString(activity, "strLoginUserId"));
                    textParamMap.put("isFingerprint", "true");
//                    password = GISharedPreUtil.getString(activity, "PWD");
//                    textParamMap.put("strPassword", password); // ????????????????????????????????????????????????2019-10-17 ?????????
                } else if (GIStringUtil.isBlank(password)) {// ??????????????????

                } else { // ??????????????????????????????
                    textParamMap.put("strUsername", username);
                    password = GIAESOperator.getInstance().encrypt(password);
                    textParamMap.put("strPassword", password);
                }
                if (GIStringUtil.isBlank(deviceCode)) {
                    deviceCode = GIPhoneUtils.getSerialNumber(activity);
                    GISharedPreUtil.setValue(activity, "deviceCode", deviceCode);
                }
                textParamMap.put("strUdid", deviceCode);
                textParamMap.put("strVersion", String.valueOf(GIPhoneUtils.getAppVersionCode(activity)));
                if (isChangeRole) {
                    textParamMap.put("intChange", "1");
                    textParamMap.put("strSid", GISharedPreUtil.getString(activity, "strSid"));
                    textParamMap.put("intIdentity", "2");
                    textParamMap.put("intUserRole", GISharedPreUtil.getInt(activity, "intUserRole") + ""); // ????????????????????????isHasLogin???false?????????????????????intUserRole
                }
                doRequestNormal(ApiManager.getInstance().doLogin(textParamMap), 0);
            }

            @Override
            public void onClose() { // ????????????????????????
                GILogUtil.e("onClose");
            }

            @Override
            public void onFinish() { // ????????????????????????
                GILogUtil.e("onFinish");
            }

            @Override
            public void onDeny(String permisson, int position) { // ??????
                GILogUtil.e("onDeny");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDLoginOutCallBackManager.getInstance().removeLast();
    }

    /**
     * ?????????????????????
     */
    private void getSmsInfo(String mobile) {
        ApiManager.refreshRetrofit();
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strMobile", mobile); // ?????????
        doRequestNormal(ApiManager.getInstance().getSmsCode(textParamMap), 2);
    }

    private void doLogic(Object obj, int sign) {
        String toastMessage = null;
        try {
            CountDownTimerUtils countDownTimers = new CountDownTimerUtils(getcode_TextView, 60000, 1000);
            switch (sign) {
                case 0: // ??????
                    LoginBean loginBean = (LoginBean) obj;
                    Utils.sp_loginIn(activity, loginBean);
                    ApiManager.refreshRetrofit();
                    EventBusCarrier eventBusCarrier = new EventBusCarrier();
                    eventBusCarrier.setObject(loginBean.getIntUserRole());
                    eventBusCarrier.setEventType(1);
                    EventBus.getDefault().post(eventBusCarrier); //??????MainActivity????????????
                    if (code_LinearLayout.getVisibility() != View.VISIBLE) { // ???????????????????????????
                        if (finger_login_LinearLayout.getVisibility() == View.VISIBLE) { // ??????????????????
                            fingerprintDialog.dismiss();
                        } else {
                            GISharedPreUtil.setValue(activity, "USER", username);
                            if (remember_CheckBox.isChecked()) {
                                GISharedPreUtil.setValue(activity, "RMBPWD", true);
                                GISharedPreUtil.setValue(activity, "PWD", password);
                            } else {
                                GISharedPreUtil.setValue(activity, "RMBPWD", false);
                                //GISharedPreUtil.setValue(activity, "PWD", "");
                            }
                        }
                    }
                    if (AppConfigUtil.isUseYouMeng(activity)) {
                        mPushAgent.addAlias(loginBean.getStrProjectName() + loginBean.getStrLoginUserId(), "username", new UTrack.ICallBack() {
                            @Override
                            public void onMessage(boolean isSuccess, String message) {
                                GILogUtil.i("addAlias#isSuccess----->" + isSuccess + "\nmessage----->" + message);
                            }
                        });
                    }
                    int intIdentity = loginBean.getIntIdentity();//???????????????????????????1- ??????;2-?????????
                    if (loginBean.getObjList() != null && loginBean.getObjList().size() > 0 && !GISharedPreUtil.getBoolean(activity, "Checked", false)) {//??????????????????????????????????????????
                        GISharedPreUtil.setValue(activity, "isHasLogin", false);
                        GISharedPreUtil.setValue(activity, "strUserId", "");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("loginBean", loginBean);
                        showActivity(activity, ChangeRoleActivity.class, bundle);
                    } else if (2 == intIdentity && !isChangeRole && !GISharedPreUtil.getBoolean(activity, "Checked", false)) {
                        GISharedPreUtil.setValue(activity, "isHasLogin", false);
                        GISharedPreUtil.setValue(activity, "strUserId", "");
                        prgDialog.closePrgDialog();
                        mQuickCustomPopup
                                .alignCenter(true)
                                .anchorView(back_TextView)
                                .showAnim(new SlideTopEnter())
                                .dismissAnim(null)
                                .offset(0, 0)
                                .dimEnabled(true)
                                .gravity(Gravity.BOTTOM)
                                .show();
                    } else {
                        prgDialog.closePrgDialog();
                        if (GIStringUtil.isNotBlank(loginBean.getStrName())) {
                            toastMessage =getResources().getString(R.string.string_welcome)+"," + loginBean.getStrName();
                        }
                        Bundle bundle = new Bundle();
                        LoginCarrier invoker = getIntent().getParcelableExtra(LoginInterceptor.mINVOKER);
                        if (invoker != null) {
                            handler.sendEmptyMessage(0);
                        } else {
                            if (0 == loginBean.getIntUserRole() || 1 == loginBean.getIntUserRole()) {
                                bundle.putInt("currentIndex", currentIndex);
                            } else {
                                bundle.putInt("currentIndex", 1);
                            }
                            skipActivity(activity, MainActivity.class, bundle);
                        }
                    }
                    break;
                case 2: // ???????????????
                    prgDialog.closePrgDialog();
                    try {
                        SmsReceiveBean smsReceiveBean = (SmsReceiveBean) obj;
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
        } catch (Exception e) {
            e.printStackTrace();
            toastMessage = getString(R.string.data_error);
        }
        if (toastMessage != null) {
            showToast(toastMessage);
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:// ????????????
                    LoginCarrier invoker = getIntent().getParcelableExtra(LoginInterceptor.mINVOKER);
                    invoker.invoke(LoginActivity.this);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * ?????????????????????
     */
    private void initFingerprint(final int sign) {
        FingerprintUtil.callFingerPrint(activity, new FingerprintUtil.OnCallBackListenr() {
            private View view;
            private TextView finger_TextView;
            private TextView zwsb_TextView;
            private TextView title_TextView;

            @Override
            public void onSupportFailed() { // ???????????????????????????
                GISharedPreUtil.setValue(activity, "isSupportFingerprint", false);
                if (sign == 0) {
                    doCancelFingerprint();
                }
            }

            @Override
            public void onInsecurity() { // ????????????????????????????????????
            }

            @Override
            public void onEnrollFailed() { //???????????????
            }

            @Override
            public void onAuthenticationStart() { // ????????????
                if (sign == 0) {
                    doCancelFingerprint();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_finger, null);
                    title_TextView = view.findViewById(R.id.tv_title);
                    title_TextView.setText(getString(R.string.app_name) + "??????");
                    finger_TextView = view.findViewById(R.id.tv_finger);
                    zwsb_TextView = view.findViewById(R.id.tv_zwsb);
                    builder.setView(view);
                    builder.setCancelable(false);
                    builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doCancelFingerprint();
                        }
                    });
                    fingerprintDialog = builder.create();
                    fingerprintDialog.show();
                }
            }

            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) { // ??????????????????
                if (sign != 0)
                    //errMsgId ??????????????????????????????
                    showToast(errString.toString());
                if (fingerprintDialog != null && fingerprintDialog.isShowing()) {
                    fingerprintDialog.dismiss();
                }
            }

            @Override
            public void onAuthenticationFailed() { // ????????????
                showToast("?????????????????????");
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                showToast(helpString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) { // ????????????
                GILogUtil.e("result:" + result.getCryptoObject());
                if (fingerprintDialog.isShowing()) {
                    finger_TextView.setText(R.string.font_fingerprint);
                    finger_TextView.setTextColor(getResources().getColor(R.color.view_head_bg));
                    zwsb_TextView.setTextColor(getResources().getColor(R.color.view_head_bg));
                    if (GIStringUtil.isBlank(GISharedPreUtil.getString(activity, "USER"))) {
                        username_EditText.requestFocus();
                        showToast("?????????????????????,??????????????????????????????");
                    } else if (GIStringUtil.isBlank(GISharedPreUtil.getString(activity, "PWD"))) {
                        password_EditText.requestFocus();
                        showToast("???????????????????????????????????????????????????");
                    } else {
                        doLogin();
                    }
                } else {
                    showToast("?????????????????????????????????");
                }
            }
        });
    }

    /**
     * ????????????????????????
     */
    private void doCancelFingerprint() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FingerprintUtil.cancel();
            }
        }, 200);
    }

    class SimpleCustomPop extends BasePopup<SimpleCustomPop> {
        private RoundImageView personHead_RoundImageView;//????????????
        private TextView title_TextView;//??????
        private Button changeMem_Button;//????????????
        private Button changeWorker_Button;//??????????????????
        private CheckBox remind_CheckBox;//????????????

        public SimpleCustomPop(Context context) {
            super(context);
            setCanceledOnTouchOutside(false);
        }

        @Override
        public View onCreatePopupView() {
            View view = View.inflate(activity, R.layout.dialog_change_role, null);
            personHead_RoundImageView = view.findViewById(R.id.iv_person_head);
            title_TextView = view.findViewById(R.id.tv_title);
            changeMem_Button = view.findViewById(R.id.btn_change_mem);
            changeWorker_Button = view.findViewById(R.id.btn_change_worker);
            remind_CheckBox = view.findViewById(R.id.cb_remind);
            return view;
        }

        @Override
        public void setUiBeforShow() {
            GIImageUtil.loadImg(activity, personHead_RoundImageView, GISharedPreUtil.getString(activity, "strPhotoUrl"), 1);
            title_TextView.setText(GISharedPreUtil.getString(activity, "strName") + "????????????????????????");
            remind_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        GISharedPreUtil.setValue(activity, "Checked", true);
                    } else {
                        GISharedPreUtil.setValue(activity, "Checked", false);
                    }
                }
            });
            // ??????
            changeMem_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isChangeRole = true;
                    GISharedPreUtil.setValue(activity, "intUserRole", 0);
                    dismiss();
                    doLogin();
                }
            });

            // ????????????
            changeWorker_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GISharedPreUtil.setValue(activity, "intUserRole", 1);
                    isChangeRole = true;
                    dismiss();
                    doLogin();
                }
            });
        }
    }

    @Override
    public void onLoginOut() {
        GILogUtil.i("IM?????????=====");
    }

    @Override
    public void onLoginOutFailed(int i, String s) {
        GILogUtil.i("IM????????????=====");
    }

    @Override
    public void onForceOut(int i) {
        GILogUtil.i("IM????????????111=====");
    }

    @Override
    public void onModifyPwd() {
        GILogUtil.i("IM????????????33333=====");
    }
}
