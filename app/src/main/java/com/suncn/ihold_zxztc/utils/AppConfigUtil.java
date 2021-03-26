package com.suncn.ihold_zxztc.utils;

import android.content.Context;

import com.gavin.giframe.utils.GISharedPreUtil;
import com.gavin.giframe.utils.GIStringUtil;

/**
 * 功能开关配置工具类
 */
public class AppConfigUtil {
    /**
     * 是否开启友盟功能
     */
    public static boolean isUseYouMeng(Context context) {
        return isUse(context, "strUseYouMeng");
    }

    /**
     * 是否开启IM功能
     */
    public static boolean isUseIM(Context context) {
        return isUse(context, "strUseQDIM");
    }

    /**
     * 是否开启启达IM功能
     */
    public static boolean isUseQDIM(Context context) {
        return isUse(context, "strUseQDIM");
    }

    /**
     * 是否开启视频会议功能
     */
    public static boolean isUseQDVideo(Context context) {
        return isUse(context, "strUseQDVideo");
    }

    /**
     * 是否开启承办单位功能
     */
    public static boolean isUseCbdw(Context context) {
        return isUse(context, "strUseCbdw");
    }

    /**
     * 是否开启承办系统功能（(审核时、待交办时有交办信息的根据分发类型显示是否有承办系统的)）
     */
    public static boolean isUseCbxt(Context context) {
        return isUse(context, "strUseCbxt");
    }

    /**
     * 是否显示协商方式
     */
    public static boolean isShowXsfs(Context context) {
        return isUse(context, "strShowXsfs");
    }

    /**
     * 是否显示调研过程
     */
    public static boolean isShowDygc(Context context) {
        return isUse(context, "strShowDygc");
    }

    /**
     * 是否显示建议承办单位
     */
    public static boolean isShowTjcbdw(Context context) {
        return isUse(context, "strShowTjcbdw");
    }

    /**
     * 是否显示预办理
     */
    public static boolean isShowCbdwPre(Context context) {
        return isUse(context, "strFlowShowCbdwPre");
    }

    /**
     * 是否显示社情民意交办
     */
    public static boolean isUseInfoDist(Context context) {
        return isUse(context, "strUseInfoDist");
    }

    /**
     * 是否显示提案提交
     */
    public static boolean isExistUserByCppccSession(Context context) {
        String value = GISharedPreUtil.getString(context, "isExistUserByCppccSession");
        if (GIStringUtil.isBlank(value)) {
            return false;
        }
        return value.equals("true");
    }

    /**
     * 是否启用附议提案选项（提案详情中的基本信息）
     */
    public static boolean isUseSupportMotion(Context context) {
        return isUse(context, "strUseSupportMotion");
    }

    /**
     * 是否启用联名提案选项（提案详情中的基本信息）
     */
    public static boolean isUseAllyMotion(Context context) {
        return isUse(context, "strUseAllyMotion");
    }

    /**
     * 是否显示雷达图
     */
    public static boolean isUseMemberExam(Context context) {
        return isUse(context, "strUseMemberExam");
    }

    /**
     * 是否显示通讯录
     */
    public static boolean isUseMobileBook(Context context) {
        return isUse(context, "strUseMobileBook");
    }

    /**
     * 是否使用二维码签到功能
     */
    public static boolean isUseSaoMQianD(Context context) {
        return isUse(context, "strUseSaoMQianD");
    }

    /**
     * 是否有编辑新闻的权限
     */
    public static boolean isCanEditNews(Context context) {
        return isUse1(context, "strNewsDelStateByAdmin");
    }

    /**
     * 是否有编辑圈子的权限
     */
    public static boolean isCanEditCircle(Context context) {
        return isUse1(context, "strCircleDelStateByAdmin");
    }

    private static boolean isUse(Context context, String key) {
        String value = GISharedPreUtil.getString(context, key);
        if (GIStringUtil.isBlank(value)) {
            return false;
        }
        return value.equals("1");
    }

    private static boolean isUse1(Context context, String key) {
        String value = GISharedPreUtil.getString(context, key);
        if (GIStringUtil.isBlank(value)) {
            return false;
        }
        return value.equals("1");
    }
}
