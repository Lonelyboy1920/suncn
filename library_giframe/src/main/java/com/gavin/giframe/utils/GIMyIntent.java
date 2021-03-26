package com.gavin.giframe.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.HashMap;

/**
 * 调用手机中已安装的软件打开文件
 */
public class GIMyIntent {
    /**
     * 判断是否是Office文件
     */
    public static boolean isOffice(String filePath) {
        String end = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
        return end.equals(".pdf") || end.equals(".doc") || end.equals(".docx") || end.equals(".wps") || end.equals(".xls") || end.equals(".xlsx") || end.equals(".et") || end.equals(".pps") || end.equals(".ppt") || end.equals(".pptx") || end.equals(".dps");
    }

    private static final HashMap<String, String> MIME_MapTable = new HashMap<String, String>() {
        {
            put(".pdf", "application/pdf");
            put(".doc", "application/msword");
            put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            put(".wps", "application/msword");
            put(".xls", "application/vnd.ms-excel");
            put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            put(".et", "application/vnd.ms-excel");
            put(".pps", "application/vnd.ms-powerpoint");
            put(".ppt", "application/vnd.ms-powerpoint");
            put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
            put(".dps", "application/vnd.ms-powerpoint");
            put(".apk", "application/vnd.android.package-archive");
            put(".exe", "application/octet-stream");
            put(".bin", "application/octet-stream");
            put(".gtar", "application/x-gtar");
            put(".gz", "application/x-gzip");
            put(".jar", "application/java-archive");
            put(".js", "application/x-javascript");
            put(".rtf", "application/rtf");
            put(".tar", "application/x-tar");
            put(".tgz", "application/x-compressed");
            put(".z", "application/x-compress");
            put(".chm", "application/x-chm");
            put(".class", "application/octet-stream");
            put(".bmp", "image/bmp");
            put(".gif", "image/gif");
            put(".jpeg", "image/jpeg");
            put(".jpg", "image/jpeg");
            put(".png", "image/png");
            put("c.", "text/plain");
            put(".conf", "text/plain");
            put(".cpp", "text/plain");
            put(".h", "text/plain");
            put(".htm", "text/html");
            put(".html", "text/html");
            put(".java", "text/plain");
            put(".log", "text/plain");
            put(".prop", "text/plain");
            put(".rc", "text/plain");
            put(".sh", "text/plain");
            put(".txt", "text/plain");
            put(".xml", "text/plain");
            put(".3gp", "video/3gpp");
            put(".asf", "video/x-ms-asf");
            put(".avi", "video/x-msvideo");
            put(".rmvb", "audio/x-pn-realaudio");
            put(".m3u", "audio/x-mpegurl");
            put(".m4a", "audio/mp4a-latm");
            put(".m4b", "audio/mp4a-latm");
            put(".m4p", "audio/mp4a-latm");
            put(".m4u", "video/vnd.mpegurl");
            put(".m4v", "video/x-m4v");
            put(".mov", "video/quicktime");
            put(".mp2", "audio/x-mpeg");
            put(".mp3", "audio/x-mpeg");
            put(".mp4", "video/mp4");
            put(".mpc", "application/vnd.mpohun.certificate");
            put(".mpe", "video/mpeg");
            put(".mpeg", "video/mpeg");
            put(".mpg", "video/mpeg");
            put(".mpg4", "video/mp4");
            put(".mpga", "audio/mpeg");
            put(".msg", "application/vnd.ms-outlook");
            put(".ogg", "audio/ogg");
            put(".wav", "audio/x-wav");
            put(".wma", "audio/x-ms-wma");
            put(".wmv", "audio/x-ms-wmv");
            put(".zip", "application/zip");
            put(".rar", "application/rar");
            put("", "*/*");
        }
    };

    /**
     * 根据文件后缀名获得对应的MIME类型。
     */
    public static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名 */
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end.equals(""))
            return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        return MIME_MapTable.get(end);
    }

    /**
     * 打开文件
     */
    public static boolean openFile(Context context, String path) {
        File file = new File(path);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW); //设置intent的Action属性
        String type = getMIMEType(file); //获取文件file的MIME类型
        Uri contentUri = GIFileProvider7.getUriForFile(context, intent, file, false);
        intent.setDataAndType(contentUri, type); //设置intent的data和Type属性。
        try {
            context.startActivity(intent); //这里最好try一下，有可能会报错。 //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
        } catch (Exception e) {
            e.printStackTrace();
            GIToastUtil.showMessage(context, "未发现可打开此文件的软件，请安装后再试！");
            return false;
        }
        return true;
    }
}
