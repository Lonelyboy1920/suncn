package com.suncn.ihold_zxztc.utils;

public class DefineUtil {
    /* 系统相关配置 */
    public static String strSid;
    public static final String websvrpwd = "suncnihold654321";
    public static final String sKey = "iholdsuncn502key"; // 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
    public static final String api_update = "ios/AutoUpdateServlet";
    public static final long FILE_SIZE = 1024 * 1024 * 5; // 最大文件5M
    public static final long IMAGE_SIZE = 1024 * 1024 * 5; // 最大图片5M
    public static final long VIDEO_SIZE = 20971520; // 最大视频20M
    public static final String FILE_MESSAGE = "单个文件不能大于5M";
    public static final String IMAGE_MESSAGE = "单个图片不能大于5M";
    public static final String VIDEO_MESSAGE = "单个视频不能大于20M";
    public static final int GLOBAL_PAGESIZE = 10; // 列表项每页显示的条数
    public static final int MAX_COUNT_5000 = 5000;
    public static final int MAX_COUNT_300 = 300;
    public static final int MAX_COUNT_500 = 500;
    public static final int MAX_COUNT_3000 = 3000;
    public static final int MAX_COUNT_50 = 50;
    public static final int MAX_COUNT_2000 = 2000;

    public static final boolean IS_OPEN_DEBUG = false; // 是否开启DEBUG模式（发布正式版本时建议设为false）
    public static final boolean IS_OPEN_DELETE_AFTER_READ = false; // 是否打开文件界面关闭清除缓存的功能
    public static final String PROTOCOL = "http://"; // 请求协议
    public static boolean isUpdate = true;
    public static boolean isNeedRefresh = false; //是否需要刷新

    public static final int zxta_signIn = 1004; // 提案签收
    public static final int zxta_lmta = 103; // 联名提案
    public static final int wdhy = 7001; // 我的会议
    public static final int hygl = 6001; // 会议管理
    public static final int hdgl = 5001; // 活动管理
    public static final int sqmy = 4001; // 社情民意管理
    public static final int hyfy = 3001; // 会议发言管理
    public static final int zhxx = -1; // 次会信息
    public static final int wdhd = 8001; // 我的活动
    public static final int xghd = 8002; // 相关活动
    public static final String suncnOpenAudio = "suncn_open_audio";//打开音频
    public static final String suncnCloseAudio = "suncn_close_audio";//关闭音频
    public static final String suncnCloseVideo = "suncn_close_video";//关闭视频
    public static final String suncnOpenVideo = "suncn_open_video";//开启视频
    public static final String suncnExitMeet = "suncn_exit_meet";//退出会议
    public static final boolean isSimpleChinese = true;

}
