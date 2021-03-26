package com.suncn.ihold_zxztc.activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIPhoneUtils;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.longchat.base.QDClient;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.util.QDAppStatusManager;
import com.suncn.ihold_zxztc.ApiManager;
import com.suncn.ihold_zxztc.MyApplication;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.activity.base.BaseActivity;
import com.suncn.ihold_zxztc.bean.LoginBean;
import com.suncn.ihold_zxztc.utils.Utils;
import com.suncn.ihold_zxztc.view.CustomVideoView;
import com.suncn.library_basic.util.MediaFile;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;

/**
 * 启动页(包含自动登录)
 */
public class SplashActivity extends BaseActivity {
    private CustomVideoView videoView;
    private long DELAY_TIME = 3000L;

    @Override
    public void setRootView() {
        if (getResources().getBoolean(R.bool.IS_OPEN_SPLASH_ROBOT) && GIStringUtil.isBlank(GISharedPreUtil.getString(activity, "strSplashUrl"))) {
            videoView = new CustomVideoView(this);
            setContentView(videoView);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            // 始终允许窗口延伸到屏幕短边上的刘海区域
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
    }

    @Override
    public void initData() {
        super.initData();
        GISharedPreUtil.setValue(activity, "QDInitSuccess", false);
        QDAppStatusManager.getInstance().setAppStatus(QDAppStatusManager.AppStatusConstant.APP_NORMAL);
        //避免崩溃后再次因为企达而崩溃，在启动页重新初始化
        QDClient.getInstance().init(getApplicationContext());
        QDLanderInfo.getInstance().init(getApplicationContext());
        GISharedPreUtil.setValue(activity, GISharedPreUtil.getString(activity, "strLoginUserId") + "isHstLogin", false);

        //阿里云查询
        // SophixManager.getInstance().queryAndLoadNewPatch();
        requestCallBack = new requestCallBack() {
            @Override
            public void onSuccess(Object data, int sign) {
                doLogic(data, sign);
            }
        };

        if (videoView != null) {
            videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash)); //设置播放加载路径
            videoView.start();//播放
        }

        String server = GISharedPreUtil.getString(this, "server");
        String port = GISharedPreUtil.getString(this, "port");
        int lastLoadVersion = GISharedPreUtil.getInt(this, "lastLoadVersion"); // 获取上次加载的版本，判断是否更换了版本号
        String lastLoadChannel = GISharedPreUtil.getString(this, "lastLoadChannel"); // 获取上次加载的渠道，判断是否更换了渠道加载
        int versionCode = GIPhoneUtils.getAppVersionCode(this);
        // 当前渠道
        String myChannel = ((MyApplication) getApplication()).myChannel;
        if (!lastLoadChannel.equals(myChannel)) {
            GISharedPreUtil.clearData(activity); // 如果切换渠道，需要清除登录数据
        }
        if (GIStringUtil.isEmpty(server) || GIStringUtil.isEmpty(port) || lastLoadVersion != versionCode || !lastLoadChannel.equals(myChannel)) { // 本次存储的数据为空或者第一次加载本版本时，调用默认数据
            GISharedPreUtil.setValue(this, "server", this.getResources().getString(R.string.default_server));
            GISharedPreUtil.setValue(this, "port", this.getResources().getString(R.string.default_port));
            GISharedPreUtil.setValue(this, "lastLoadVersion", versionCode);
            GISharedPreUtil.setValue(this, "lastLoadChannel", myChannel);

            SharedPreferences spf = this.getSharedPreferences("SERVER_SETTING", Context.MODE_PRIVATE);
            Map<String, ?> map = spf.getAll();
            if (map == null || map.size() == 0) { // 此处待优化
                spf.edit().putString("serverName0", this.getResources().getString(R.string.default_server)).apply();
                spf.edit().putString("serverPort0", this.getResources().getString(R.string.default_port)).apply();
            }
        }
        doLogin();
        String languagename = Locale.getDefault().getDisplayLanguage();
        String country = Locale.getDefault().getDisplayCountry();
        Log.i("==============", "languagename:" + languagename + "country:" + country);
        GISharedPreUtil.setValue(this, "isSimpleChinase", "中国".equalsIgnoreCase(country) ? true : false);
        if (GIStringUtil.isNotBlank(country)
                && GIStringUtil.isNotBlank(GISharedPreUtil.getString(this, "country", "中国"))
                && country.equalsIgnoreCase(GISharedPreUtil.getString(this, "country", "中国"))) {

        } else {
            //如果系统语言变换了，需要刷新Retrofit配置isSimpleChinese(boolean isSimpleChinase)，否则后台返回数据无法进行语言的切换
            GISharedPreUtil.setValue(this, "country", country);
            ApiManager.refreshRetrofit();
        }
    }

    /**
     * 登陆操作
     */
    private void doLogin() {
        HiPermission.create(activity).checkSinglePermission(Manifest.permission.READ_PHONE_STATE, new PermissionCallback() {
            @Override
            public void onGuarantee(String permisson, int position) { // 同意/已授权
                String deviceCode = GIPhoneUtils.getSerialNumber(activity); // 获取设备号
//                if (GIStringUtil.isBlank(deviceCode)) {
//                    showToast("设备号获取失败，无法进行登录，请检查权限！");
//                } else {
                    GISharedPreUtil.setValue(activity, "deviceCode", deviceCode);
                    textParamMap = new HashMap<>();
                    textParamMap.put("strUdid", deviceCode);
                    textParamMap.put("intType", "20");
                    textParamMap.put("strVersion", String.valueOf(GIPhoneUtils.getAppVersionCode(activity)));
                    doRequestNormal(ApiManager.getInstance().doLogin(textParamMap), 0);
//                }
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

    private void doLogic(Object data, int sign) {
        String toastMessage = null;
        try {
            switch (sign) {
                case 0:
                    LoginBean loginBean = (LoginBean) data;
                    Utils.sp_loginIn(activity, loginBean);
                    if (GIStringUtil.isNotBlank(GISharedPreUtil.getString(activity, "strSplashUrl"))) {
                        DELAY_TIME = 100L;
                    }
                    redirectByTime();
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            toastMessage = getString(R.string.data_error);
        }
        if (GIStringUtil.isNotEmpty(toastMessage))
            showToast(toastMessage);
    }

    /**
     * 延迟一段时间后跳转到登录页
     */
    private void redirectByTime() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (GIStringUtil.isNotBlank(GISharedPreUtil.getString(activity, "strSplashUrl"))) {
                    if (MediaFile.isVideoFileType(Utils.formatFileUrl(activity, GISharedPreUtil.getString(activity, "strSplashUrl")))) {
                        skipActivity(activity, SplashVideoActivity.class);
                    } else {
                        skipActivity(activity, SplashWebViewActivity.class);
                    }
                } else if (getResources().getBoolean(R.bool.IS_OPEN_VISITOR) || GISharedPreUtil.getBoolean(activity, "isHasLogin")) {
                    skipActivity(activity, MainActivity.class);
                } else {
                    skipActivity(activity, LoginActivity.class);
                }
                overridePendingTransition(R.anim.anim_activity_splash_in, R.anim.anim_activity_splash_out);
            }
        }, DELAY_TIME);
    }
}
