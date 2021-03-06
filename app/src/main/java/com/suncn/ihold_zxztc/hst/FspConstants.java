package com.suncn.ihold_zxztc.hst;

import com.hst.fsp.VideoProfile;

public class FspConstants {

    public static final int LOCAL_VIDEO_CLOSED = 0;   ///<本地视频关闭状态
    public static final int LOCAL_VIDEO_BACK_PUBLISHED = 1;  ///<广播了前置摄像头
    public static final int LOCAL_VIDEO_FRONT_PUBLISHED = 2;  ///<广播了后置摄像头


    public static final String PKEY_USER_APPID = "userAppId";
    public static final String PKEY_USER_APPSECRET = "userAppSecret";
    public static final String PKEY_USER_APPSERVERADDR = "userServerAddr";
    public static final String PKEY_USE_DEFAULT_APPCONFIG = "useDefaultAppConfig";
    public static final String PKEY_USE_DEFAULT_OPENCAMERA = "useDefaultOpenCamera";
    public static final String PKEY_USE_DEFAULT_OPENMIC = "useDefaultOpenMic";


    // 为安全起见，App Secret最好不要在客户端保存
    public static final String DEFAULT_APP_ID = "";
    public static final String DEFAULT_APP_SECRET = "";
    public static final String DEFAULT_APP_ADDRESS = "";


    public static final VideoProfile DEFAULT_PROFILE = new VideoProfile(640, 480, 15);

}
