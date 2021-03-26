package com.qd.longchat.util;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/1/10 下午5:15
 */

public class QDAppStatusManager {

    public static QDAppStatusManager mInstance = null;
    private int appStatus = AppStatusConstant.APP_FORCE_KILLED;

    public static QDAppStatusManager getInstance() {
        if (mInstance == null) {
            synchronized (QDAppStatusManager.class) {
                if (mInstance == null)
                    mInstance = new QDAppStatusManager();
            }
        }
        return mInstance;
    }

    public void setAppStatus(int appStatus) {
        this.appStatus = appStatus;
    }

    public int getAppStatus() {
        return appStatus;
    }

    public class AppStatusConstant {
        public static final int APP_FORCE_KILLED = 0;
        public static final int APP_NORMAL = 1;
    }
}
