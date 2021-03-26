package com.suncn.ihold_zxztc.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.gavin.giframe.utils.GISharedPreUtil;

import java.util.Locale;

/**
 * @author :Sea
 * Date :2020-5-15 10:41
 * PackageName:com.suncn.ihold_zxztc.utils
 * Desc: 检测系统语言是否修改工具类
 */
public class LanguageUtil {
    private static final String LAST_LANGUAGE = "lastLanguage";

    /**
     * 当改变系统语言时,重启App
     *
     * @param activity
     * @param homeActivityCls 主activity
     * @return
     */
    public static boolean isLanguageChanged(Activity activity, Class<?> homeActivityCls) {
        Locale locale = Locale.getDefault();
        if (locale == null) {
            return false;
        }
        String localeStr = GISharedPreUtil.getString(activity, LAST_LANGUAGE);
        String curLocaleStr = getLocaleString(locale);
        if (TextUtils.isEmpty(localeStr)) {
            GISharedPreUtil.setValue(activity, LAST_LANGUAGE, curLocaleStr);
            return false;
        } else {
            if (localeStr.equals(curLocaleStr)) {
                return false;
            } else {
                GISharedPreUtil.setValue(activity, LAST_LANGUAGE, curLocaleStr);
                restartApp(activity, homeActivityCls);
                return true;
            }
        }
    }

    private static String getLocaleString(Locale locale) {
        if (locale == null) {
            return "";
        } else {
            return locale.getCountry() + locale.getLanguage();
        }
    }

    public static void restartApp(Activity activity, Class<?> homeClass) {
        Intent intent = new Intent(activity, homeClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        // 杀掉进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
