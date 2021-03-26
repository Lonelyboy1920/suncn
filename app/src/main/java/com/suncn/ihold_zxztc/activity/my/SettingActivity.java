package com.suncn.ihold_zxztc.activity.my;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceEnter;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.ZoomExit.ZoomOutExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.gavin.giframe.utils.GIFileCst;
import com.gavin.giframe.utils.GIFileUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.longchat.base.QDClient;
import com.longchat.base.callback.QDLoginOutCallBack;
import com.longchat.base.manager.listener.QDLoginOutCallBackManager;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.util.QDBitmapUtil;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.LoginActivity;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.databinding.ActivitySettingBinding;
import com.suncn.ihold_zxztc.interfaces.Presenter;
import com.suncn.ihold_zxztc.utils.AppConfigUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengCallback;
import com.umeng.message.MessageSharedPrefs;
import com.umeng.message.UTrack;

import java.io.File;
import java.util.HashMap;

import me.leolin.shortcutbadger.ShortcutBadger;
import skin.support.content.res.SkinCompatResources;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity implements Presenter, QDLoginOutCallBack {
    private ActivitySettingBinding binding;
    private boolean isPush_status;
    private SharedPreferences tempSharedPreference;
    private String filePath;
    private boolean isOnRestart;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                reQuestLoginOut();
            } else if (requestCode == 1) { // 开启/关闭指纹识别
                isOnRestart = true;
                binding.switchFinger.setChecked(GISharedPreUtil.getBoolean(activity, "isOpenFingerLogin_" + GISharedPreUtil.getString(activity, "strUserId")));
                isOnRestart = false;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setRootView() {
        isShowBackBtn = true;
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_setting);
        binding.setPresenter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != binding.switchCompat) {
            binding.switchCompat.setThumbDrawable(SkinCompatResources.getDrawable(activity, R.drawable.thumb_selector));
            binding.switchCompat.setTrackDrawable(SkinCompatResources.getDrawable(activity, R.drawable.track_selector));
        }
    }

    @Override
    public void initData() {
        super.initData();
        setHeadTitle(getString(R.string.string_set));
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                reLogin();
            }
        };
        filePath = GIFileUtil.getRootDir(activity) + File.separator + GIFileCst.DIR_APP;
        tempSharedPreference = getSharedPreferences("push_status", Context.MODE_PRIVATE);
        isPush_status = tempSharedPreference.getBoolean("status", true);
        binding.switchCompat.setChecked(isPush_status);
        QDLoginOutCallBackManager.getInstance().addCallBack(this);
        if (AppConfigUtil.isUseYouMeng(activity)) {
            binding.llMessage.setVisibility(View.VISIBLE);
        } else {
            binding.llMessage.setVisibility(View.GONE);
        }
        if (GISharedPreUtil.getBoolean(activity, "isSupportFingerprint", true)) { // 支持指纹识别
            binding.llFinger.setVisibility(View.VISIBLE);
            // 需求设定：指纹识别的开关跟用户进行关联（ios端会重置上一次登录的用户指纹开关状态，Android未做此处理）
            binding.switchFinger.setChecked(GISharedPreUtil.getBoolean(activity, "isOpenFingerLogin_" + GISharedPreUtil.getString(activity, "strUserId")));
        } else {
            binding.llFinger.setVisibility(View.GONE);
        }
        binding.llFinger.setVisibility(View.GONE); // 指纹识别逻辑有问题，暂时关闭
        try {
            binding.tvCache.setText(GIFileUtil.formetFileSize(GIFileUtil.getFolderSize(new File(filePath))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        binding.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPush_status = isChecked;
                if (isPush_status) {
                    //开启推送并设置开启的回调处理
                    mPushAgent.enable(new IUmengCallback() {
                        @Override
                        public void onSuccess() {
                            MessageSharedPrefs.getInstance(activity).setIsEnabled(true);
                            tempSharedPreference.edit().putBoolean("status", true).commit();
                        }

                        @Override
                        public void onFailure(String s, String s1) {
                            showToast(getString(R.string.string_open_failed));
                        }
                    });
                } else {
                    //关闭推送并设置关闭的回调处理
                    mPushAgent.disable(new IUmengCallback() {
                        @Override
                        public void onSuccess() {
                            tempSharedPreference.edit().putBoolean("status", false).commit();
                        }

                        @Override
                        public void onFailure(String s, String s1) {
                            showToast(getString(R.string.string_close_failed));
                        }
                    });
                }
            }
        });

        binding.switchFinger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showActivity(activity, CheckFingerActivity.class, 1);
                } else {
                    GISharedPreUtil.setValue(activity, "isOpenFingerLogin_" + GISharedPreUtil.getString(activity, "strUserId"), false);
                    if (!isOnRestart) {
                        showToast(getString(R.string.string_fingerprint_login_is_turned_off));
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_loginout: // 退出
                showConfirmDialog(0);
                break;
            case R.id.ll_changePwd: // 修改密码
                showActivity(activity, ModifyPwdActivity.class, 0);
                break;
            case R.id.ll_clear_cache: // 清除缓存
                showConfirmDialog(1);
                break;
            case R.id.llChangeSkin:
                showActivity(this, ChangeSkinActivity.class);
                break;
            case R.id.llChangeFontSize:
                showActivity(this, ChangeFontSizeActivity.class);
                break;
        }
    }

    /**
     * 对话框
     */
    private void showConfirmDialog(final int type) {
        String title;
        if (type == 0) {
            title = getString(R.string.string_are_you_sure_to_login_out);
        } else {
            title = getString(R.string.string_are_you_sure_to_clear_cache);
        }
        BaseAnimatorSet mBasIn = new BounceTopEnter();
        BaseAnimatorSet mBasOut = new BounceTopEnter();
        try {
            mBasIn = BounceEnter.class.newInstance();
            mBasOut = ZoomOutExit.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final MaterialDialog dialog = new MaterialDialog(activity);
        dialog.title(getString(R.string.string_tips)).content(title).btnText(getString(R.string.string_cancle), getString(R.string.string_determine)).showAnim(mBasIn).dismissAnim(mBasOut).show();
        dialog.setOnBtnClickL(
                new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }, new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        if (type == 0) {
                            dialog.superDismiss();
                            reQuestLoginOut();
                        } else {
                            dialog.dismiss();
                            clearApplicationData();
                        }

                    }
                }
        );
    }

    /**
     * 执行清楚缓存
     */
    private void clearApplicationData() {
        boolean isSuccess = GIFileUtil.deleteFile(filePath);
        try {
            if (isSuccess) {
                showToast(getString(R.string.string_clear_cache_success));
            } else {
                showToast(getString(R.string.string_clear_cache_failed));
            }
            binding.tvCache.setText(GIFileUtil.formetFileSize(GIFileUtil.getFolderSize(new File(filePath))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reQuestLoginOut() {
        textParamMap = new HashMap<>();
        doRequestNormal(ApiManager.getInstance().doLoginOut(textParamMap), 0);
    }

    /**
     * 执行注销登录
     */
    private void reLogin() {
        String strProjectName = GISharedPreUtil.getString(activity, "strProjectName");
        String strLoginUserId = GISharedPreUtil.getString(activity, "strLoginUserId");
        mPushAgent.deleteAlias(strProjectName + strLoginUserId, "username", new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean isSuccess, String message) {
                GILogUtil.i("deleteAlias#isSuccess----->" + isSuccess + "\nmessage----->" + message);
            }
        });
        MobclickAgent.onProfileSignOff();
        ShortcutBadger.removeCount(activity);
        if (AppConfigUtil.isUseQDIM(activity) && GISharedPreUtil.getBoolean(activity, "QDInitSuccess")) {
            if (QDClient.getInstance().isOnline()) {//注销启达的服务
                QDClient.getInstance().loginOut();
            } else {
                QDLanderInfo.getInstance().loginOut();
                QDBitmapUtil.getInstance().logOut();
            }
            Utils.sp_loginOut(activity); // 如果注销登录，需要清除登录数据
        } else {
            Utils.sp_loginOut(activity); // 如果注销登录，需要清除登录数据
            Bundle bundle = new Bundle();
            bundle.putBoolean("isReLogin", true);
            skipActivity(activity, LoginActivity.class, bundle);
        }

    }

    @Override
    public void onLoginOut() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isReLogin", true);
        skipActivity(activity, LoginActivity.class, bundle);
    }

    @Override
    public void onLoginOutFailed(int i, String s) {

    }

    @Override
    public void onForceOut(int i) {

    }

    @Override
    public void onModifyPwd() {

    }
}
