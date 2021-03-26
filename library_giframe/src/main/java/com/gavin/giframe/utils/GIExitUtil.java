package com.gavin.giframe.utils;

import android.app.Activity;
import android.widget.Toast;

import com.gavin.giframe.base.GIActivityStack;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 退出程序
 */
public class GIExitUtil {
    private Boolean isQuit = false;
    Timer timer = new Timer();
    private static final GIExitUtil instance = new GIExitUtil();

    public GIExitUtil() {
    }

    public static GIExitUtil create() {
        return instance;
    }

    public void exit(Activity activity) {
        if (!isQuit) {
            isQuit = true;
//            Toast.makeText(activity, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
            GIToastUtil.showMessage(activity, "再按一次返回键退出程序");
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    isQuit = false;
                }
            };
            timer.schedule(task, 2000);
        } else {
            GISharedPreUtil.setValue(activity, "strClickInfo", "-1");
            GIActivityStack.getInstance().AppExit();
        }
    }
}
