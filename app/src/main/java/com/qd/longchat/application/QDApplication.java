package com.qd.longchat.application;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.longchat.base.QDClient;
import com.longchat.base.util.QDLog;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.qd.longchat.R;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.util.QDWXShare;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/10 下午3:16
 */

public class QDApplication extends Application {

    public static final String TAG = "com.xiaomi.mipushdemo";
    private boolean isRegister;

    private String huaweiToken;
    private String xiaomiToken;
    private int refCount = 0;


    public static DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.ic_download_loading) // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.mipmap.ic_download_failed) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.mipmap.ic_download_failed) // 设置图片加载或解码过程中发生错误显示的图片
            .resetViewBeforeLoading(false)  // default 设置图片在加载前是否重置、复位
            .delayBeforeLoading(100)  // 下载前的延迟时间
            .cacheInMemory(true) // default  设置下载的图片是否缓存在内存中
            .cacheOnDisc(true) // default  设置下载的图片是否缓存在SD卡中
            .bitmapConfig(Bitmap.Config.RGB_565)//设置为RGB565比起默认的ARGB_8888要节省大量的内存
            .build();

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

    @Override
    public void onCreate() {
        super.onCreate();
        QDClient.getInstance().init(getApplicationContext());
        QDLanderInfo.getInstance().init(getApplicationContext());

        QbSdk.initX5Environment(getApplicationContext(), cb);

        SDKInitializer.initialize(getApplicationContext());

        QDWXShare.getInstance().init(getApplicationContext());
        QDWXShare.getInstance().register();

        initImageLoader();
        registerActivityLifecycleCallbacks(callbacks);
        CrashReport.initCrashReport(getApplicationContext(), "854ba60adb", false);

    }


    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions 内存缓存文件的最大长宽
                .threadPriority(Thread.NORM_PRIORITY - 2) // default 设置当前线程的优先级
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .threadPoolSize(3)//线程池内加载的数量
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024)) //可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)  // 内存缓存的最大值
                .discCacheSize(50 * 1024 * 1024) // 50 Mb sd卡(本地)缓存的最大值
                .discCacheFileCount(100)  // 可以缓存的文件数量
                // default为使用HASHCODE对UIL进行加密命名， 还可以用MD5(new Md5FileNameGenerator())加密
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .writeDebugLogs() // 打印debug log
                .build(); //开始构建
        ImageLoader.getInstance().init(config);
    }


    public String getHuaweiToken() {
        return huaweiToken;
    }

    public String getXiaomiToken() {
        return xiaomiToken;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        QDWXShare.getInstance().unregister();

//        disConnectHuaweiSercer();
        unregisterActivityLifecycleCallbacks(callbacks);
        System.exit(0);
    }

    public int getRefCount() {
        return refCount;
    }

}
