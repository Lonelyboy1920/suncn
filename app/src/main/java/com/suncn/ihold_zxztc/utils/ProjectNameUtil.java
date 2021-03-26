package com.suncn.ihold_zxztc.utils;

import android.content.Context;

import com.gavin.giframe.utils.GIUtil;

/**
 * 获取当前渠道的项目
 */
public class ProjectNameUtil {
    /**
     * 是否为产品
     */
    public static boolean isIhold(Context context) {
        return getProject(context, "ihold_zxztc");
    }

    /**
     * 是否为贵阳市政协项目
     */
    public static boolean isGYSZX(Context context) {
        return getProject(context, "gyszx");
    }

    /**
     * 是否为贵州省政协项目
     */
    public static boolean isGZSZX(Context context) {
        return getProject(context, "pro_gzszx");
    }

    /**
     * 是否为苏州市政协项目
     */
    public static boolean isSZSZX(Context context) {
        return getProject(context, "szszx");
    }

    /**
     * 是否为江门市政协项目
     */
    public static boolean isJMSZX(Context context) {
        return getProject(context, "jmszx");
    }

    /**
     * 是否为荔湾区政协项目
     */
    public static boolean isLWQZX(Context context) {
        return getProject(context, "lwqzx");
    }

    /**
     * 是否为滁州市政协项目
     */
    public static boolean isCZSZX(Context context) {
        return getProject(context, "czszx");
    }

    private static boolean getProject(Context context, String name) {
        String myChannel = GIUtil.getChannelName(context); // 当前渠道
        return myChannel.equals(name);
    }
}
