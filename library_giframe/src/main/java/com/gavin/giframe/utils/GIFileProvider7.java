package com.gavin.giframe.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * Android7.0+ FileProvider适配
 * 调用：Uri contentUri = GIFileProvider7.getUriForFile(context, intent, file, false);
 */
public class GIFileProvider7 {
    public static Uri getUriForFile(Context context, Intent intent, File file, boolean writeAble) {
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            //给Uri授予临时读写权限（BuildConfig.APPLICATION_ID为项目包名）
            context.grantUriPermission(context.getPackageName(), contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            contentUri = Uri.fromFile(file);
        }
        return contentUri;
    }
}