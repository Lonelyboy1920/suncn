package com.suncn.ihold_zxztc;
import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.ErrorCode;
import com.alibaba.sdk.android.feedback.util.FeedbackErrorCallback;
import com.baidu.mapapi.SDKInitializer;
import com.danikula.videocache.HttpProxyCacheServer;
import com.gavin.giframe.authcode.GIAESOperator;
import com.gavin.giframe.base.GIApplication;
import com.gavin.giframe.crash.CaocConfig;
import com.gavin.giframe.crash.CustomActivityOnCrash;
import com.gavin.giframe.utils.GIDensityUtil;
import com.gavin.giframe.utils.GIFontUtil;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIMyIntent;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIToastUtil;
import com.gavin.giframe.utils.GIUtil;
import com.goyourfly.multi_picture.ImageLoader;
import com.goyourfly.multi_picture.MultiPictureView;
import com.iflytek.cloud.SpeechUtility;
import com.longchat.base.QDClient;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.model.QDAVCmd;
import com.longchat.base.util.QDLog;
import com.qd.longchat.activity.QDAVActivity;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.suncn.ihold_zxztc.activity.SplashActivity;
import com.suncn.ihold_zxztc.rxhttp.update.OKHttpUpdateHttpService;
import com.suncn.ihold_zxztc.skinloader.CustomSDCardLoader;
import com.suncn.ihold_zxztc.skinloader.ZipSDCardLoader;
import com.suncn.ihold_zxztc.utils.DefineUtil;
import com.suncn.ihold_zxztc.utils.Utils;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.DownloadEntity;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnInstallListener;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import skin.support.SkinCompatManager;
import skin.support.app.SkinAppCompatViewInflater;
import skin.support.app.SkinCardViewInflater;
import skin.support.constraint.app.SkinConstraintViewInflater;
import skin.support.content.res.SkinCompatResources;
import skin.support.design.app.SkinMaterialViewInflater;

public class MyApplication extends GIApplication {
    public PushAgent mPushAgent;
    public String myChannel; // 当前渠道
    private boolean isRegister;//启达的IM视频广播是否注册
    private Context context;
    private HttpProxyCacheServer proxy;
    public static MyApplication myApplication;
    private int refCount = 0;
    public static HttpProxyCacheServer getProxy(Context context) {
        MyApplication app = (MyApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheFilesCount(3)
                .build();
    }

    public int getRefCount() {
        return refCount;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        myApplication = this;
        myChannel = GIUtil.getChannelName(this); // 获取当前的渠道（项目）名
        GIFontUtil.setDefaultFont(this); // 全局自定义APP默认字体
        GIAESOperator.sKey = DefineUtil.sKey; // 赋值加密用的Key。
        SpeechUtility.createUtility(this, "appid=" + getString(R.string.xf_mscAppId)); // 初始化科大讯飞语音识别服务
        SDKInitializer.initialize(getApplicationContext()); // 初始化百度地图控件服务（QDApplication中已进行初始化，但此处仍需要初始化，否则签到定位会崩溃）

        Utils.switchDebugMode(this, GISharedPreUtil.getBoolean(this, "isDebugMode"));// DEBUG模式开关
        if (!BuildConfig.DEBUG) { // 直接运行为DEBUG模式，不进行异常收集
            initCrash(); //初始化全局异常崩溃
        }
        initUMPush(); // 初始化友盟推送
        initUMShare(); //初始化友盟分享
        initUpdate(); // 初始化检查更新
        initFeedbackService(); // 初始化阿里百川用户反馈
//        GIUtil.closeAndroidPDialog();  //屏蔽安卓9.0系统出现弹窗，目前无需进行此设置
        //内存泄漏检测
//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            LeakCanary.install(this);
//        }
        QbSdk.initX5Environment(getApplicationContext(), cb);
        initChangeSkin();
        initMultiPicture();
        registerActivityLifecycleCallbacks(callbacks);
    }
    private QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
        @Override
        public void onCoreInitFinished() {
            QDLog.i("QDApplication", "QbSdk core init finished");
        }

        @Override
        public void onViewInitFinished(boolean b) {
            QDLog.i("QDApplication", "QbSdk init finished:" + b);
        }
    };
    /**
     * @author :Sea
     * Desc: 初始化换肤配置
     */
    private void initChangeSkin() {
        SkinCompatManager.withoutActivity(this)
                .addStrategy(new CustomSDCardLoader())          // 自定义加载策略，指定SDCard路径
                .addStrategy(new ZipSDCardLoader())             // 自定义加载策略，获取zip包中的资源
                .addInflater(new SkinAppCompatViewInflater())   // 基础控件换肤
                .addInflater(new SkinMaterialViewInflater())    // material design
                .addInflater(new SkinConstraintViewInflater())  // ConstraintLayout
                .addInflater(new SkinCardViewInflater())        // CardView v7
//                .addInflater(new SkinCircleImageViewInflater()) // hdodenhof/CircleImageView
//                .addInflater(new SkinFlycoTabLayoutInflater())  // H07000223/FlycoTabLayout
                .setSkinStatusBarColorEnable(true)              // 关闭状态栏换肤
//                .setSkinWindowBackgroundEnable(false)           // 关闭windowBackground换肤
//                .setSkinAllActivityEnable(false)                // true: 默认所有的Activity都换肤; false: 只有实现SkinCompatSupportable接口的Activity换肤
                .loadSkin();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /**
     * 初始化全局异常崩溃
     */
    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
//                .errorDrawable(R.mipmap.ic_launcher) //错误图标
                .restartActivity(SplashActivity.class) //重新启动后的activity
//                .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
//                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
        //如果没有任何配置，程序崩溃显示的是默认的设置
        CustomActivityOnCrash.install(this);
    }

    /**
     * 初始化友盟推送服务
     */
    private void initUMPush() {
        HuaWeiRegister.register(this); // 华为Push初始化
        MiPushRegistar.register(this, getResources().getString(R.string.xiaomi_appid), getResources().getString(R.string.xiaomi_appkey)); // 小米Push初始化
        MeizuRegister.register(this, getResources().getString(R.string.meizu_appid), getResources().getString(R.string.meizu_appkey)); // 魅族Push初始化
        OppoRegister.register(this, getResources().getString(R.string.oppo_appkey), getResources().getString(R.string.oppo_message_secret));//OPPO通道，参数1为app key，参数2为app secret
        UMConfigure.init(this, getResources().getString(R.string.umeng_appkey), myChannel, UMConfigure.DEVICE_TYPE_PHONE, getResources().getString(R.string.umeng_message_secret)); // 初始化友盟推送服务
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);  // 场景类型设置
        MobclickAgent.setSecret(this, getResources().getString(R.string.umeng_message_secret));
        UMConfigure.setLogEnabled(GISharedPreUtil.getBoolean(this, "isDebugMode")); // 设置组件化的Log开关（参数: boolean 默认为false，如需查看LOG设置为true）
        /* 注册友盟推送服务 */
        mPushAgent = PushAgent.getInstance(this);
        //mPushAgent.setResourcePackageName(getClass().getPackage().getName()); // 当资源包名和应用程序包名不一致时，调用设置资源包名的接口
        mPushAgent.setDisplayNotificationNumber(5); // 设置通知栏最多显示通知的条数
//        mPushAgent.setNotificaitonOnForeground(true); // 应用在前台时否显示通知，true为显示，默认为true
        mPushAgent.register(new IUmengRegisterCallback() { // 注册推送服务，每次调用register方法都会回调该接口
            @Override
            public void onSuccess(String deviceToken) {
                GILogUtil.i("deviceToken----->" + deviceToken);
                initQD(); //启达的服务必须在友盟初始化之后进行
            }

            @Override
            public void onFailure(String s, String s1) {
                GILogUtil.e("消息推送服务注册失败！s=" + s + "s1=" + s1);
                initQD(); //启达的服务必须在友盟初始化之后进行
            }
        });
        mPushAgent.setMessageHandler(new UmengMessageHandler() { // 自定义参数，用于在桌面显示待办条数
            @Override
            public Notification getNotification(Context context, UMessage uMessage) {
                Map<String, String> myMap = uMessage.extra;
                if (myMap != null) {
                    String s = myMap.get("badgeCount");
                    if (GIStringUtil.isNotBlank(s)) {
                        int badgeCount = Integer.parseInt(s);
                        GIUtil.sendBadgeNumber(getApplicationContext(), R.mipmap.ic_launcher, badgeCount);
                    }
                }
                return super.getNotification(context, uMessage);
            }
        });
    }


    /**
     * 初始化启达IM服务
     */
    private void initQD() {
        QDClient.getInstance().init(getApplicationContext());
        QDLanderInfo.getInstance().init(getApplicationContext());

        // 注册启达IM推送相关广播
        if (isRegister) {
            return;
        }
        isRegister = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.qd.longchat.avcall");
        registerReceiver(receiver, filter);
    }

    /**
     * 启达IM推送相关广播
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("com.qd.longchat.avcall".equalsIgnoreCase(action)) {
                QDAVCmd cmd = intent.getParcelableExtra("AvCmd");
                String cmdName = cmd.getCmdName();
                if (GISharedPreUtil.getBoolean(getBaseContext(), "isInCall")) {
                    QDUser user = QDUserHelper.getUserById(cmd.getSendID());
                    if (cmdName.equalsIgnoreCase(QDAVCmd.CMD_NAME_VIDEO_REQ)) {
                        QDClient.getInstance().replyAVCall(cmd, user, true, "对方忙", QDAVCmd.CMD_REPLY_BUSY);
                    } else {
                        QDClient.getInstance().replyAVCall(cmd, user, false, "对方忙", QDAVCmd.CMD_REPLY_BUSY);
                    }
                    return;
                }
                if (cmdName.equalsIgnoreCase(QDAVCmd.CMD_NAME_VIDEO_REQ)) {
                    Intent avIntent = new Intent(context, QDAVActivity.class);
                    avIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID, cmd.getSendID());
                    avIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_AV_CMD, cmd);
                    avIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(avIntent);
                } else if (cmdName.equalsIgnoreCase(QDAVCmd.CMD_NAME_AUDIO_REQ)) {
                    Intent avIntent = new Intent(context, QDAVActivity.class);
                    avIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID, cmd.getSendID());
                    avIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_AV_CMD, cmd);
                    avIntent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_AUDIO, true);
                    avIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(avIntent);
                }
            }
        }
    };

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterPush();
        unregisterActivityLifecycleCallbacks(callbacks);
    }

    private void unregisterPush() {
        if (isRegister) {
            unregisterReceiver(receiver);
            isRegister = false;
        }
    }

    /**
     * 初始化友盟分享
     */
    public static void initUMShare() {
        try {
            Class<?> aClass = Class.forName("com.umeng.commonsdk.UMConfigure");
            Field[] fs = aClass.getDeclaredFields();
            for (Field f : fs) {
                GILogUtil.i("xxxxxx", "ff=" + f.getName() + "   " + f.getType().getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        //  UMConfigure.init(context, "596c298007fe657d70001444", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, ""); //友盟-履职掌中宝 --- 推送里已注册
        PlatformConfig.setSinaWeibo("1483742440", "c9445b9c470a5236f8a2e1a3dc87c599", "http://sns.whalecloud.com"); //新浪微博-名声圈
        PlatformConfig.setWeixin("wxabb00815cfe5c0d6", "24e321ac9c176f675a5931961ec34a9e");
        PlatformConfig.setQQZone("101855081", "891de60ef7cf7070bc9831c40b42734f");
        PlatformConfig.setDing("dingoahddb6mszupjz75cs");
    }

    /**
     * 初始化检查更新工具
     */
    private void initUpdate() {
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("strAppKey", getPackageName());
        paramsMap.put("intType", "20");
        paramsMap.put("intVersionCode", UpdateUtils.getVersionCode(this));         //设置默认公共请求参数
//        paramsMap.put("intVersionCode", -1);

        StringBuilder str_buf = new StringBuilder();
        String key;
        Object value;
        for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            str_buf.append(key).append("=").append(value).append("&");
        }
        GILogUtil.i("检查更新公共请求参数----->" + str_buf.toString());
        XUpdate.get()
                .debug(true)
                .isWifiOnly(false)                                              //默认设置只在wifi下检查版本更新
                .isGet(false)                                                   //默认设置使用get请求检查版本
                .isAutoMode(false)                                              //默认设置非自动模式，可根据具体使用配置
                .params(paramsMap)                                              //设置默认公共请求参数
                .setOnUpdateFailureListener(new OnUpdateFailureListener() {     //设置版本更新出错的监听
                    @Override
                    public void onFailure(UpdateError error) {
                        if (error.getCode() != UpdateError.ERROR.CHECK_NO_NEW_VERSION) {          //对不同错误进行处理
                            GIToastUtil.showMessage(getApplicationContext(), error.getMessage());
                        }
                    }
                })
                .supportSilentInstall(false)                                     //设置是否支持静默安装，默认是true
                .setIUpdateHttpService(new OKHttpUpdateHttpService())           //这个必须设置！实现网络请求功能。
                .setOnInstallListener(new OnInstallListener() {
                    @Override
                    public boolean onInstallApk(@NonNull Context context, @NonNull File apkFile, @NonNull DownloadEntity downloadEntity) {
                        GIMyIntent.openFile(context, apkFile.getPath());
                        return true;
                    }

                    @Override
                    public void onInstallApkSuccess() {

                    }
                })
                .init(this); //这个必须初始化
    }

    /**
     * 初始化阿里百川用户反馈
     */
    private void initFeedbackService() {
        FeedbackAPI.init(this, getString(R.string.aliyun_appkey), getString(R.string.aliyun_secret));
        /**
         * 添加自定义的error handler
         */
        FeedbackAPI.addErrorCallback(new FeedbackErrorCallback() {
            @Override
            public void onError(Context context, String errorMessage, ErrorCode code) {
                Toast.makeText(context, "ErrMsg is: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        FeedbackAPI.addLeaveCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                Log.d("DemoApplication", "custom leave callback");
                return null;
            }
        });
        /**
         * 在Activity的onCreate中执行的代码
         * 可以设置状态栏背景颜色和图标颜色，这里使用com.githang:status-bar-compat来实现
         */
        //FeedbackAPI.setActivityCallback(new IActivityCallback() {
        //    @Override
        //    public void onCreate(Activity activity) {
        //        StatusBarCompat.setStatusBarColor(activity,getResources().getColor(R.color.aliwx_setting_bg_nor),true);
        //    }
        //});
        /**
         * 自定义参数演示
         */
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("loginTime", "登录时间");
            jsonObject.put("visitPath", "登陆，关于，反馈");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FeedbackAPI.setAppExtInfo(jsonObject);
        /**
         * 以下是设置UI
         */
        //设置默认联系方式
        FeedbackAPI.setDefaultUserContactInfo("");
        //沉浸式任务栏，控制台设置为true之后此方法才能生效
        FeedbackAPI.setTranslucent(true);
        //设置标题栏"历史反馈"的字号，需要将控制台中此字号设置为0
        FeedbackAPI.setHistoryTextSize(20);
        //设置标题栏高度，单位为像素
        FeedbackAPI.setTitleBarHeight(100);
        //设置返回按钮图标
        FeedbackAPI.setBackIcon(R.mipmap.close_icon);
    }

    /**
     * 初始化MultiPicture控件
     */
    private void initMultiPicture() {
        MultiPictureView.setImageLoader(new ImageLoader() {
            @Override
            public void loadImage(@NotNull ImageView imageView, @NotNull Uri uri) {
                GIImageUtil.loadImg(context, uri, imageView, context.getResources().getDrawable(R.mipmap.img_error));
            }
        });
    }

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NotNull
            @Override
            public RefreshHeader createRefreshHeader(@NotNull Context context, @NotNull RefreshLayout layout) {
                //默认是 BezierRadarHeader
                MaterialHeader materialHeader = new MaterialHeader(context);
                materialHeader.setColorSchemeColors(SkinCompatResources.getColor(context, R.color.view_head_bg));
                return materialHeader;
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NotNull
            @Override
            public RefreshFooter createRefreshFooter(@NotNull Context context, @NotNull RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                ClassicsFooter classicsFooter = new ClassicsFooter(context);
                classicsFooter.setPadding(0, GIDensityUtil.dip2px(context, 10), 0, GIDensityUtil.dip2px(context, 10));
                return classicsFooter;
            }
        });
    }
    private ActivityLifecycleCallbacks callbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            refCount++;
            QDClient.getInstance().setRefCount(refCount);
            QDLog.e("QDApplicaiton", "the count is:" + refCount);
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            refCount--;
            QDClient.getInstance().setRefCount(refCount);
            QDLog.e("QDApplication", "the count is:" + refCount);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };
    //SophixStubApplication中已调用
//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
////        MultiDex.install(this);
//    }
}
