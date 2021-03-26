package com.gavin.giframe.base;

import android.app.Application;

import androidx.annotation.NonNull;

import com.gavin.giframe.utils.GIUtil;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastAliPayStyle;

public class GIApplication extends Application {
    private static Application sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        setApplication(this);
        ToastUtils.init(this, new ToastAliPayStyle(this));
    }

    /**
     * 当主工程没有继承BaseApplication时，可以使用setApplication方法初始化BaseApplication
     */
    public static synchronized void setApplication(@NonNull Application application) {
        sInstance = application;
        //初始化工具类
        GIUtil.init(application);
        //注册监听每个activity的生命周期,便于堆栈式管理
        application.registerActivityLifecycleCallbacks(new com.gavin.giframe.ActivityLifecycleCallbacks() {
            @Override
            protected void onTargetActivityDestroyed() {

            }
        });
    }

    /**
     * 获得当前app运行的Application
     */
    public static Application getInstance() {
        if (sInstance == null) {
            throw new NullPointerException("please inherit GIApplication or call setApplication.");
        }
        return sInstance;
    }
}
