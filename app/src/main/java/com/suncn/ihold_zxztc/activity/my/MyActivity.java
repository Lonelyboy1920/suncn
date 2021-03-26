package com.suncn.ihold_zxztc.activity.my;

import android.content.Context;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.flyco.dialog.widget.popup.base.BasePopup;
import com.gavin.giframe.utils.GIPhoneUtils;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.widget.RoundImageView;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.ChangeRoleActivity;
import com.suncn.ihold_zxztc.activity.H5WebViewActivity;
import com.suncn.ihold_zxztc.activity.application.meeting.QRCodeSignInActivity;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.activity.circle.CirclePersonPageActivity;
import com.suncn.ihold_zxztc.activity.global.WebViewActivity;
import com.suncn.ihold_zxztc.bean.EventBusCarrier;
import com.suncn.ihold_zxztc.bean.LoginBean;
import com.suncn.ihold_zxztc.databinding.ActivityMyBinding;
import com.suncn.ihold_zxztc.interfaces.Presenter;
import com.suncn.ihold_zxztc.rxhttp.update.MyUpdateParser;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.ProjectNameUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.proxy.impl.DefaultUpdateChecker;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import skin.support.content.res.SkinCompatResources;

/**
 * 我的
 */
public class MyActivity extends BaseActivity implements Presenter {
    private SimpleCustomPop mQuickCustomPopup;
    private int intIdentity;//该用户有几种身份（1一种2两种）
    private ActivityMyBinding binding;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            initData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (GISharedPreUtil.getString(activity, "strShowDutyCard").equals("1")) {
            binding.tvQrcode.setVisibility(View.VISIBLE);
        } else {
            binding.tvQrcode.setVisibility(View.GONE);
        }
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_my);
        binding.setPresenter(this);
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle(getString(R.string.string_my));
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };
        binding.tvParty.setText(GISharedPreUtil.getString(activity, "strFaction"));
        binding.tvSector.setText(GISharedPreUtil.getString(activity, "strSector"));
        String strVersionName = GIPhoneUtils.getAppVersionName(activity);
        binding.tvVersionName.setText(getString(R.string.string_version_v) + strVersionName);
        binding.tvName.setText(GISharedPreUtil.getString(activity, "strName"));
        if (GISharedPreUtil.getBoolean(activity, "isAppHasUpdate")) {
            binding.tvVersionNew.setVisibility(View.VISIBLE);
        } else {
            binding.tvVersionNew.setVisibility(View.GONE);
        }
        intIdentity = GISharedPreUtil.getInt(activity, "intIdentity");
        int intUserRole = GISharedPreUtil.getInt(activity, "intUserRole");
        if (intUserRole == 2 || intUserRole == 3 || GISharedPreUtil.getInt(activity, "intGroup") == 1) {
            binding.llCircle.setVisibility(View.GONE);
        } else {
            binding.llCircle.setVisibility(View.VISIBLE);
        }
        if (0 == intUserRole && GISharedPreUtil.getInt(activity, "intGroup") != 1) {//是委员不是集体
            binding.tvParty.setVisibility(View.VISIBLE);
            binding.tvSector.setVisibility(View.VISIBLE);
        } else {
            binding.tvParty.setVisibility(View.INVISIBLE);
            binding.tvSector.setVisibility(View.GONE);
        }
        if ("0".equals(GISharedPreUtil.getString(activity, "strIsCollective"))) {
            binding.llFollow.setVisibility(View.VISIBLE);
            binding.llBirth.setVisibility(View.VISIBLE);
        } else {
            binding.llFollow.setVisibility(View.GONE);
            binding.llBirth.setVisibility(View.GONE);
        }
        if (GISharedPreUtil.getString(activity, "strShowDutyCard").equals("1")) {
            binding.tvQrcode.setVisibility(View.VISIBLE);
        } else {
            binding.tvQrcode.setVisibility(View.GONE);
        }
        if (GISharedPreUtil.getString(activity, "strShowRootUnit").equals("1")) {
            binding.tvRole.setText(GISharedPreUtil.getString(activity, "strDefalutRootUnitName"));
            binding.tvRoleRight.setVisibility(View.VISIBLE);
        } else {
            if (2 != intIdentity) {
                binding.tvRoleRight.setVisibility(View.GONE);
                binding.tvRole.setText(GISharedPreUtil.getString(activity, "strLoginUserType"));
                //binding.llChangeRole.setVisibility(View.GONE);
            } else {
                binding.tvRoleRight.setVisibility(View.VISIBLE);
                binding.tvRole.setText(GISharedPreUtil.getString(activity, "strLoginUserType"));
            }
        }
        if (ProjectNameUtil.isGYSZX(activity)) {
            binding.llCollection.setVisibility(View.GONE);
        }
        binding.tvRoleRight.setTextColor(SkinCompatResources.getColor(activity, R.color.view_head_bg));
        binding.tvAbout.setText("  " + getString(R.string.string_about) + getString(R.string.app_name));
        showHeadPhoto(GISharedPreUtil.getString(activity, "strPhotoUrl"), binding.ivPersonHead);
        mQuickCustomPopup = new SimpleCustomPop(activity);

        if (ProjectNameUtil.isJMSZX(activity)) {
            binding.tvCircle.setText(R.string.string_mine_group);
        }
//        checkUpdate();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.ll_change_role:
                if (GISharedPreUtil.getString(activity, "strShowRootUnit").equals("1")) {
                    showActivity(activity, ChangeRoleActivity.class, 0);
                } else if (2 == intIdentity) {
                    mQuickCustomPopup
                            .showAnim(null)
                            .dismissAnim(null)
                            .anchorView(binding.llChangeRole)
                            .offset(-5, -10)
                            .dimEnabled(true)
                            .gravity(Gravity.BOTTOM)
                            .show();
                }
                break;
            case R.id.iv_person_head://点击头像跳转个人信息
                showActivity(activity, InfoActivity.class);
                break;
            case R.id.ll_feedback: // 意见反馈
                FeedbackAPI.openFeedbackActivity();
                FeedbackAPI.setDefaultUserContactInfo(GISharedPreUtil.getString(activity, "strMobile"));
                break;
            case R.id.ll_update: // 版本更新
//                new CheckUpdateUtil(activity, autoUpdate, 1);
                if (GISharedPreUtil.getBoolean(activity, "isAppHasUpdate")) {
                    checkUpdate();
                } else {
                    showToast(getString(R.string.xupdate_error_check_no_new_version));
                }
                return;
            case R.id.ll_about: // 关于
                showActivity(activity, WebViewActivity.class);
                break;
            case R.id.ll_collection://我的收藏
                showActivity(activity, CollectionListActivity.class);
                break;
            case R.id.tv_qrcode://电子履职卡
                showActivity(activity, QRCodeSignInActivity.class);
                break;
            case R.id.ll_setting://设置
                showActivity(activity, SettingActivity.class);
                break;
            case R.id.ll_circle:
                bundle.putString("strUserId", GISharedPreUtil.getString(activity, "strUserId"));
                bundle.putBoolean("isShowChat", false);
//                showActivity(activity, Circle_PersonalPageActivity.class, bundle);
                showActivity(activity, CirclePersonPageActivity.class, bundle);
                break;
            case R.id.ll_birth:
                bundle.putBoolean("isNeedTitle", false);
                bundle.putString("strUrl", Utils.formatFileUrl(activity, "res/dist/index.html#/birthdayList?strSid=") + GISharedPreUtil.getString(activity, "strSid"));
                showActivity(activity, H5WebViewActivity.class, bundle);
                break;
            case R.id.ll_follow:
                bundle.putBoolean("isNeedTitle", false);
                bundle.putString("strUrl", Utils.formatFileUrl(activity, "res/dist/index.html#/getFlowersMy?strSid=") + GISharedPreUtil.getString(activity, "strSid"));
                showActivity(activity, H5WebViewActivity.class, bundle);
                break;
            default:
                break;
        }
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
//        textParamMap = new HashMap<String, String>();
//        textParamMap.put("intType", "20");
//        doRequestNormal(ApiManager.getInstance().checkUpdates(textParamMap), 0);
        String mUpdateUrl = Utils.formatFileUrl(activity, DefineUtil.api_update);
        XUpdate.newBuild(activity)
                .updateUrl(mUpdateUrl)
                .updateParser(new MyUpdateParser(activity))
                .updateChecker(new DefaultUpdateChecker() {
                    @Override
                    public void onBeforeCheck() {
                        super.onBeforeCheck();
                        prgDialog.showUpdateDialog();
                    }

                    @Override
                    public void onAfterCheck() {
                        super.onAfterCheck();
                        prgDialog.closePrgDialog();
                    }
                })
                .update();
    }

    /**
     * 登录请求（以指纹识别的模式进行登录）
     */
    private void doLogin(int intUserRole) {
        textParamMap = new HashMap<String, String>();
        textParamMap.put("intChange", 1 + "");
        textParamMap.put("intIdentity", 2 + "");
        textParamMap.put("strUsername", GISharedPreUtil.getString(activity, "strLoginUserId"));
//        textParamMap.put("strPassword", GISharedPreUtil.getString(activity, "PWD"));
        textParamMap.put("isFingerprint", "true");
        textParamMap.put("strUdid", GISharedPreUtil.getString(activity, "deviceCode"));
        textParamMap.put("strVersion", String.valueOf(GIPhoneUtils.getAppVersionCode(activity)));
        textParamMap.put("intUserRole", intUserRole + ""); // 必传------ 用户类型（0-委员；1-工作人员；2-承办单位；3承办系统）,默认委员0
        GISharedPreUtil.setValue(activity, "resetUserRole", true);
        doRequestNormal(ApiManager.getInstance().doLogin(textParamMap), 1);
    }

    /**
     * 请求结果
     */
    private void doLogic(Object obj, int what) {
        String toastMessage = null;
        prgDialog.closePrgDialog();
        switch (what) {
//            case 0:
//                try {
//                    autoUpdate = (AutoUpdateBean) obj;
//                    int nativeCode = GIPhoneUtils.getAppVersionCode(activity);
//                    int versionCode = autoUpdate.getIntVersionCode();
//                    if (versionCode > nativeCode) {
//                        binding.tvVersionNew.setVisibility(View.VISIBLE);
//                    } else {
//                        binding.tvVersionNew.setVisibility(View.GONE);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    toastMessage = getString(R.string.data_error);
//                }
//                break;
            case 1://切换角色
                try {
                    LoginBean loginBean = (LoginBean) obj;
                    int intUserRole = loginBean.getIntUserRole();
                    if (0 == intUserRole && GISharedPreUtil.getInt(activity, "intGroup") != 1) {
                        binding.tvParty.setVisibility(View.VISIBLE);
                        binding.tvSector.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvParty.setVisibility(View.GONE);
                        binding.tvSector.setVisibility(View.GONE);
                    }
                    GISharedPreUtil.setValue(activity, "intUserRole", intUserRole);
                    GISharedPreUtil.setValue(activity, "strSid", loginBean.getStrSid());
                    EventBusCarrier eventBusCarrier = new EventBusCarrier();
                    eventBusCarrier.setEventType(1);
                    eventBusCarrier.setObject(intUserRole);
                    EventBus.getDefault().post(eventBusCarrier); //通知MainActivity刷新数据
                    toastMessage = getString(R.string.string_switchover_success);
                    if (GISharedPreUtil.getString(activity, "strShowDutyCard").equals("1")) {
                        binding.tvQrcode.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvQrcode.setVisibility(View.GONE);
                    }
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

    class SimpleCustomPop extends BasePopup<SimpleCustomPop> {
        private RoundImageView personHead_RoundImageView;//圆形头像
        private TextView title_TextView;//姓名
        private Button changeMem_Button;//切换委员
        private Button changeWorker_Button;//切换工作人员
        private CheckBox remind_CheckBox;//不在提示

        public SimpleCustomPop(Context context) {
            super(context);
            setCanceledOnTouchOutside(true);
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
            showHeadPhoto(GISharedPreUtil.getString(activity, "strPhotoUrl"), personHead_RoundImageView);
            title_TextView.setText(GISharedPreUtil.getString(activity, "strName") + getString(R.string.string_welcome_to_login_use));
            if (GISharedPreUtil.getBoolean(activity, "Checked", false)) {
                remind_CheckBox.setChecked(true);
            } else {
                remind_CheckBox.setChecked(false);
            }
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
            changeMem_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int intUserRole = GISharedPreUtil.getInt(activity, "intUserRole");
                    if (intUserRole == 0) {
                        dismiss();
                    } else {
                        dismiss();
                        doLogin(0);
                        binding.tvRole.setText(R.string.string_current_role_commissioner);
                        //msg_TextView.setText("市政协委员");
                    }
                }
            });
            changeWorker_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int intUserRole = GISharedPreUtil.getInt(activity, "intUserRole");
                    if (intUserRole == 1) {
                        dismiss();
                    } else {
                        /*Intent intent = new Intent("com.suncn.ihold_zxztc.role_change");
                        intent.putExtra("intUserRole", 1);
                        sendBroadcast(intent);*/
                        dismiss();
                        doLogin(1);
                        binding.tvRole.setText(R.string.string_current_role_agency_personnel);
                        //msg_TextView.setText("政协工作人员");
                    }
                }
            });
        }
    }
}
