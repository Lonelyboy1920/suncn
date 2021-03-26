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
 * 登陆界面
 */
public class LoginActivity extends BaseActivity implements QDLoginOutCallBack {
    @BindView(id = R.id.img_head)
    private ImageView head_ImageView;
    @BindView(id = R.id.tv_debug, click = true)
    private TextView debug_TextView; // DEBUG模式开关按钮（彩蛋）
    @BindView(id = R.id.tv_back, click = true)
    private GITextView back_TextView;//右上角返回按钮
    @BindView(id = R.id.et_username)
    private ClearEditText username_EditText; // 用户名EditText
    @BindView(id = R.id.et_password)
    private ClearEditText password_EditText; // 密码EditText
    @BindView(id = R.id.et_code)
    private ClearEditText code_EditText; // 短信登录EditText
    @BindView(id = R.id.tv_getcode, click = true)
    private TextView getcode_TextView;
    @BindView(id = R.id.cb_remember)
    private CheckBox remember_CheckBox; // 记住密码CheckBox
    @BindView(id = R.id.tv_pwd_visual, click = true)
    private TextView pwdVisual_TextView;//是否显示密码
    @BindView(id = R.id.tv_forget_pwd, click = true)
    private TextView forget_pwd_TextView;
    @BindView(id = R.id.tv_setting, click = true)
    private TextView setting_TextView; // 设置TextView
    @BindView(id = R.id.tv_finger_icon)
    private TextView finger_icon_TextView;
    @BindView(id = R.id.tv_user)
    private TextView user_TextView;
    @BindView(id = R.id.btn_finger_userlogin, click = true)
    private Button finger_userlogin_Button;//指纹登录页面中账号登录
    @BindView(id = R.id.btn_finger_smslogin, click = true)
    private Button finger_smslogin_Button;//指纹登录页面短信验证登录
    @BindView(id = R.id.ll_finger_login)
    private LinearLayout finger_login_LinearLayout;//指纹登录页面LinearLayout
    @BindView(id = R.id.ll_normal_login)
    private LinearLayout normal_login_LinearLayout;//未设置指纹登录页面
    @BindView(id = R.id.ll_pwd)
    private LinearLayout pwd_LinearLayout;//密码布局LinearLayout
    @BindView(id = R.id.ll_code)
    private LinearLayout code_LinearLayout;//验证码布局LinearLayout
    @BindView(id = R.id.ll_repwd)
    private LinearLayout repwd_LinearLayout;//记住密码布局LinearLayout

    @BindView(id = R.id.iv_login, click = true)
    private ImageView login_Button;//账号密码登录
    @BindView(id = R.id.btn_smslogin, click = true)
    private Button smsLogin_Button;//短信验证登录
    @BindView(id = R.id.iv_finger_login, click = true)
    private ImageView finger_login_ImageView;//指纹登录
    @BindView(id = R.id.tvLogin, click = true)
    private TextView tvLogin;

    private AlertDialog fingerprintDialog;//指纹识别的dialog

    private String deviceCode; // 设备号
    private String username; // 用户名
    private String password; // 密码
    private boolean isVisible; // 密码是否可见
    private int myClickCount; // 背景单击次数统计
    private String server; // OA ip
    private String port; // OA端口
    private int currentIndex = 2;
    private SimpleCustomPop mQuickCustomPopup;
    private boolean isChangeRole = false;
    private boolean isNeedExit; // 点击返回按钮需要执行退出APP的操作
    private boolean isReLogin; // 是否是主动退出登录

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 点击软键盘以外的区域，关闭软键盘
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

        if (isNeedExit) { // 点击返回按钮需要执行退出APP的操作
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

        if (GISharedPreUtil.getBoolean(activity, "isOpenFingerLogin_" + GISharedPreUtil.getString(activity, "strUserId"))) {// 此账号是否开启指纹登录功能
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
            username_EditText.setHint("请输入手机号");
            pwd_LinearLayout.setVisibility(View.GONE);
            repwd_LinearLayout.setVisibility(View.INVISIBLE);
            smsLogin_Button.setText("账号密码登录");
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
            case R.id.tv_getcode: // 获取验证码
                String mobile = username_EditText.getText().toString().trim();
                if (GIStringUtil.isBlank(mobile)) {
                    username_EditText.requestFocus();
                    showToast("请输入手机号！");
                } else if (!GIStringUtil.isMobile(mobile)) {//正则判断输入是不是手机号
                    showToast("请输入正确的手机号！");
                } else {
                    getSmsInfo(mobile);
                }
                break;
            case R.id.tv_pwd_visual: // 密码显示隐藏
                if (isVisible) { // 隐藏密码
                    isVisible = false;
                    pwdVisual_TextView.setText(R.string.fontion_pwd_gone);
                    password_EditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else { // 显示密码
                    isVisible = true;
                    pwdVisual_TextView.setText(R.string.fontion_pwd_visible);
                    password_EditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                GISharedPreUtil.setValue(activity, "isVisible", isVisible);
                break;
            case R.id.tv_setting: // 设置按钮单击事件
                Login_SettingDialog instructDialog = new Login_SettingDialog(activity);
                instructDialog.show();
                break;
            case R.id.btn_finger_userlogin: // 切换到账号密码登录
                finger_login_LinearLayout.setVisibility(View.GONE);
                normal_login_LinearLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_finger_smslogin: // 切换到手机验证登录
                finger_login_LinearLayout.setVisibility(View.GONE);
                normal_login_LinearLayout.setVisibility(View.VISIBLE);
                pwd_LinearLayout.setVisibility(View.GONE);
                code_LinearLayout.setVisibility(View.VISIBLE);
                repwd_LinearLayout.setVisibility(View.GONE);
                break;
            case R.id.tvLogin:
            case R.id.iv_login: // 登录
                if (code_LinearLayout.getVisibility() == View.VISIBLE) {
                    username = username_EditText.getText().toString();
                    if (GIStringUtil.isBlank(username)) {
                        username_EditText.requestFocus();
                        showToast("请输入手机号！");
                    } else if (GIStringUtil.isBlank(code_EditText.getText().toString().trim())) {
                        code_EditText.requestFocus();
                        showToast("请输入验证码！");
                    } else {
                        doLogin();
                    }
                } else {
                    username = username_EditText.getText().toString();
                    password = password_EditText.getText().toString();
                    if (GIStringUtil.isBlank(username)) {
                        username_EditText.requestFocus();
                        showToast("请输入用户名！");
                    } else if (GIStringUtil.isBlank(password)) {
                        password_EditText.requestFocus();
                        showToast("请输入密码！");
                    } else {
                        doLogin();
                    }
                }
                break;
            case R.id.btn_smslogin: // 手机验证登录/账号密码登录切换
                String btnText = smsLogin_Button.getText().toString().trim();
                if (btnText.equals("手机验证登录")) {
                    code_LinearLayout.setVisibility(View.VISIBLE);
                    finger_login_LinearLayout.setVisibility(View.GONE);
                    normal_login_LinearLayout.setVisibility(View.VISIBLE);
                    //repwd_LinearLayout.setVisibility(View.GONE);
                    username_EditText.setText(GISharedPreUtil.getString(activity, "strMobile"));
                    username_EditText.setHint("请输入手机号");
                    pwd_LinearLayout.setVisibility(View.GONE);
                    repwd_LinearLayout.setVisibility(View.INVISIBLE);
                    smsLogin_Button.setText("账号密码登录");
                }
                if (btnText.equals("账号密码登录")) {
                    code_LinearLayout.setVisibility(View.GONE);
                    finger_login_LinearLayout.setVisibility(View.GONE);
                    normal_login_LinearLayout.setVisibility(View.VISIBLE);
                    username_EditText.setText(GISharedPreUtil.getString(activity, "USER"));
                    username_EditText.setHint("请输入用户名/手机号");
                    pwd_LinearLayout.setVisibility(View.VISIBLE);
                    repwd_LinearLayout.setVisibility(View.VISIBLE);
                    smsLogin_Button.setText("手机验证登录");
                }
                break;
            case R.id.iv_finger_login: // 指纹识别登录
                initFingerprint(1);
                break;
            case R.id.tv_forget_pwd:
                showActivity(activity, ForgetPwdActivity.class);
                break;
            case R.id.tv_debug: // 隐藏按钮单击事件
                if (!DefineUtil.IS_OPEN_DEBUG) { // 如果非DEBUG模式，则判断是否手动开启
                    myClickCount++;
                    if (myClickCount == 5) { // 单击5次
                        if (GISharedPreUtil.getBoolean(activity, "isDebugMode")) {
                            showToast("BEBUG模式已关闭！");
                            setting_TextView.setVisibility(View.GONE);
                            Utils.switchDebugMode(this, false);// DEBUG模式开关
                        } else {
                            showToast("BEBUG模式已开启！");
                            setting_TextView.setVisibility(View.VISIBLE);
                            Utils.switchDebugMode(this, true);// DEBUG模式开关
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
     * 执行返回操作
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
     * 判断是否是DEBUG模式
     */
    private void isDebugMode() {
        if (!DefineUtil.IS_OPEN_DEBUG) { // 如果非DEBUG模式，则判断是否手动开启
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
     * 登陆操作
     **/
    private void doLogin() {
        ApiManager.refreshRetrofit();
        HiPermission.create(activity).checkSinglePermission(Manifest.permission.READ_PHONE_STATE, new PermissionCallback() {
            @Override
            public void onGuarantee(String permisson, int position) { // 同意/已授权
                prgDialog.showLoadDialog();
                textParamMap = new HashMap<>();
                textParamMap.put("intType", "20");
                if (code_LinearLayout.getVisibility() == View.VISIBLE) { // 手机验证码登录方式
                    textParamMap.put("strMobile", username);
                    textParamMap.put("strMsgId", GISharedPreUtil.getString(activity, "strMsgId"));
                    textParamMap.put("strExNo", code_EditText.getText().toString().trim());//验证码
                } else if (finger_login_LinearLayout.getVisibility() == View.VISIBLE) { // 指纹识别登录方式
                    textParamMap.put("strUsername", GISharedPreUtil.getString(activity, "strLoginUserId"));
                    textParamMap.put("isFingerprint", "true");
//                    password = GISharedPreUtil.getString(activity, "PWD");
//                    textParamMap.put("strPassword", password); // 指纹识别不需要传密码参数，来自：2019-10-17 吴振超
                } else if (GIStringUtil.isBlank(password)) {// 游客模式登录

                } else { // 用户名、密码登录方式
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
                    textParamMap.put("intUserRole", GISharedPreUtil.getInt(activity, "intUserRole") + ""); // 此处选择角色时，isHasLogin为false，所以需单独传intUserRole
                }
                doRequestNormal(ApiManager.getInstance().doLogin(textParamMap), 0);
            }

            @Override
            public void onClose() { // 用户关闭权限申请
                GILogUtil.e("onClose");
            }

            @Override
            public void onFinish() { // 所有权限申请完成
                GILogUtil.e("onFinish");
            }

            @Override
            public void onDeny(String permisson, int position) { // 拒绝
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
     * 获取短信验证码
     */
    private void getSmsInfo(String mobile) {
        ApiManager.refreshRetrofit();
        prgDialog.showLoadDialog();
        textParamMap = new HashMap<>();
        textParamMap.put("strMobile", mobile); // 手机号
        doRequestNormal(ApiManager.getInstance().getSmsCode(textParamMap), 2);
    }

    private void doLogic(Object obj, int sign) {
        String toastMessage = null;
        try {
            CountDownTimerUtils countDownTimers = new CountDownTimerUtils(getcode_TextView, 60000, 1000);
            switch (sign) {
                case 0: // 登录
                    LoginBean loginBean = (LoginBean) obj;
                    Utils.sp_loginIn(activity, loginBean);
                    ApiManager.refreshRetrofit();
                    EventBusCarrier eventBusCarrier = new EventBusCarrier();
                    eventBusCarrier.setObject(loginBean.getIntUserRole());
                    eventBusCarrier.setEventType(1);
                    EventBus.getDefault().post(eventBusCarrier); //通知MainActivity刷新数据
                    if (code_LinearLayout.getVisibility() != View.VISIBLE) { // 不是手机验证码登录
                        if (finger_login_LinearLayout.getVisibility() == View.VISIBLE) { // 指纹识别登录
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
                    int intIdentity = loginBean.getIntIdentity();//该用户有几种身份（1- 一种;2-两种）
                    if (loginBean.getObjList() != null && loginBean.getObjList().size() > 0 && !GISharedPreUtil.getBoolean(activity, "Checked", false)) {//产品里面的切换角色是单独界面
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
                case 2: // 获取验证码
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
                case 0:// 登录成功
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
     * 初始化指纹识别
     */
    private void initFingerprint(final int sign) {
        FingerprintUtil.callFingerPrint(activity, new FingerprintUtil.OnCallBackListenr() {
            private View view;
            private TextView finger_TextView;
            private TextView zwsb_TextView;
            private TextView title_TextView;

            @Override
            public void onSupportFailed() { // 当前设备不支持指纹
                GISharedPreUtil.setValue(activity, "isSupportFingerprint", false);
                if (sign == 0) {
                    doCancelFingerprint();
                }
            }

            @Override
            public void onInsecurity() { // 当前设备未处于安全保护中
            }

            @Override
            public void onEnrollFailed() { //未设置指纹
            }

            @Override
            public void onAuthenticationStart() { // 准备识别
                if (sign == 0) {
                    doCancelFingerprint();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_finger, null);
                    title_TextView = view.findViewById(R.id.tv_title);
                    title_TextView.setText(getString(R.string.app_name) + "登录");
                    finger_TextView = view.findViewById(R.id.tv_finger);
                    zwsb_TextView = view.findViewById(R.id.tv_zwsb);
                    builder.setView(view);
                    builder.setCancelable(false);
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
            public void onAuthenticationError(int errMsgId, CharSequence errString) { // 识别出现错误
                if (sign != 0)
                    //errMsgId 改参数为超过验证次数
                    showToast(errString.toString());
                if (fingerprintDialog != null && fingerprintDialog.isShowing()) {
                    fingerprintDialog.dismiss();
                }
            }

            @Override
            public void onAuthenticationFailed() { // 识别失败
                showToast("指纹识别失败！");
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                showToast(helpString.toString());
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) { // 识别成功
                GILogUtil.e("result:" + result.getCryptoObject());
                if (fingerprintDialog.isShowing()) {
                    finger_TextView.setText(R.string.font_fingerprint);
                    finger_TextView.setTextColor(getResources().getColor(R.color.view_head_bg));
                    zwsb_TextView.setTextColor(getResources().getColor(R.color.view_head_bg));
                    if (GIStringUtil.isBlank(GISharedPreUtil.getString(activity, "USER"))) {
                        username_EditText.requestFocus();
                        showToast("用户名已被清除,请选择其他方式登录！");
                    } else if (GIStringUtil.isBlank(GISharedPreUtil.getString(activity, "PWD"))) {
                        password_EditText.requestFocus();
                        showToast("密码已被清除，请选择其他方式登录！");
                    } else {
                        doLogin();
                    }
                } else {
                    showToast("系统错误，请稍后再试！");
                }
            }
        });
    }

    /**
     * 取消指纹识别监听
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
        private RoundImageView personHead_RoundImageView;//圆形头像
        private TextView title_TextView;//姓名
        private Button changeMem_Button;//切换委员
        private Button changeWorker_Button;//切换工作人员
        private CheckBox remind_CheckBox;//不在提示

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
            title_TextView.setText(GISharedPreUtil.getString(activity, "strName") + "，欢迎您登录使用");
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
            // 委员
            changeMem_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isChangeRole = true;
                    GISharedPreUtil.setValue(activity, "intUserRole", 0);
                    dismiss();
                    doLogin();
                }
            });

            // 工作人员
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
        GILogUtil.i("IM退成功=====");
    }

    @Override
    public void onLoginOutFailed(int i, String s) {
        GILogUtil.i("IM退出失败=====");
    }

    @Override
    public void onForceOut(int i) {
        GILogUtil.i("IM退出失败111=====");
    }

    @Override
    public void onModifyPwd() {
        GILogUtil.i("IM退出失败33333=====");
    }
}
