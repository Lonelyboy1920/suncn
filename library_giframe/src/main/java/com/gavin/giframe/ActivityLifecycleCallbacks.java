package com.gavin.giframe;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.gavin.giframe.base.GIActivityStack;

/**
 * 注册监听每个activity的生命周期,便于堆栈式管理
 */
public abstract class ActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private Activity mTargetActivity = null;

    public ActivityLifecycleCallbacks(Activity targetActivity) {
        mTargetActivity = targetActivity;
    }

    public ActivityLifecycleCallbacks() {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        GIActivityStack.getInstance().addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        GIActivityStack.getInstance().removeActivity(activity);
        if (activity == mTargetActivity) {
            mTargetActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
            onTargetActivityDestroyed();
        }
    }

    protected abstract void onTargetActivityDestroyed();
}
