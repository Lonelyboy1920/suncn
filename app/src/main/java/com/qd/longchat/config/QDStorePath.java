package com.qd.longchat.config;

import android.content.Context;
import android.os.Environment;

import com.qd.longchat.R;

import java.io.File;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/18 下午3:17
 */

public class QDStorePath {

    private static QDStorePath instance;

    public static String MSG_FILE_PATH; //消息文件存储路径
    public static String IMG_PATH;  //图片存储路径
    public static String APP_PATH;  //应用文件存储路径
    public static String TEMP_PATH;  //临时文件存储路径
    public static String MSG_VOICE_PATH; //语音存储路径
    public static String MSG_VIDEO_PATH; //小视频存储路径
    public static String EMOJOY_PATH; //表情存储路径
    public static String COLLECT_PATH; //收藏文件路径
    public static String ICON_PATH; //头像存放路径
    public static String CLOUD_PATH; //云盘文件存放路径
    public static String UPGRADE_FOLDER;

    public static QDStorePath getInstance() {
        if (instance == null) {
            instance = new QDStorePath();
        }
        return instance;
    }
    public void init(Context context, String userId, String ssid) {
        String genPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getResources().getString(R.string.app_name) + "/";
        String userPath = genPath + userId + "_" + ssid;
        createFolder(genPath);
        createFolder(userPath);
        String[] paths = context.getResources().getStringArray(R.array.folder_name);
        MSG_FILE_PATH = userPath + "/" + paths[0] + "/";
        createFolder(MSG_FILE_PATH);
        IMG_PATH = userPath + "/" + paths[1] + "/";
        createFolder(IMG_PATH);
        APP_PATH = userPath + "/" + paths[2] + "/";
        createFolder(APP_PATH);
        TEMP_PATH = userPath + "/" + paths[3] + "/";
        createFolder(TEMP_PATH);
        MSG_VOICE_PATH = userPath + "/" + paths[4] + "/";
        createFolder(MSG_VOICE_PATH);
        MSG_VIDEO_PATH = userPath + "/" + paths[5] + "/";
        createFolder(MSG_VIDEO_PATH);
        EMOJOY_PATH = userPath + "/" + paths[6] + "/";
        createFolder(EMOJOY_PATH);
        COLLECT_PATH = userPath + "/" + paths[7] + "/";
        createFolder(COLLECT_PATH);
        ICON_PATH = userPath + "/" + paths[8] + "/";
        createFolder(ICON_PATH);
        CLOUD_PATH = userPath + "/" + paths[9] + "/";
        createFolder(CLOUD_PATH);
        UPGRADE_FOLDER = userPath + "/" + paths[10] + "/";
        createFolder(UPGRADE_FOLDER);
    }

    private void createFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }
}
