package com.qd.longchat.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/18 上午9:52
 *
 * 将图片和视频保存到相册
 */
public class QDAlbumUtil {

    public static void insertPicToAlbum(Context context, String filePath) {
        File file = new File(filePath);
        ContentResolver localContentResolver = context.getContentResolver();
        ContentValues localContentValues = getImageContentValues(file, System.currentTimeMillis());
        localContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);

        Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        final Uri localUri = Uri.fromFile(file);
        localIntent.setData(localUri);
        context.sendBroadcast(localIntent);
    }

    private static ContentValues getImageContentValues(File paramFile, long paramLong) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "image/jpeg");
        localContentValues.put("datetaken", paramLong);
        localContentValues.put("date_modified", paramLong);
        localContentValues.put("date_added", paramLong);
        localContentValues.put("orientation", 0);
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", paramFile.length());
        return localContentValues;
    }

    public static void insertVideoToAlbum(Context context, String filePath) {
        File file = new File(filePath);
        ContentResolver localContentResolver = context.getContentResolver();
        ContentValues localContentValues = getVideoContentValues(file, System.currentTimeMillis());
        localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
    }

    public static ContentValues getVideoContentValues(File paramFile, long paramLong) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "video/3gp");
        localContentValues.put("datetaken", paramLong);
        localContentValues.put("date_modified", paramLong);
        localContentValues.put("date_added", paramLong);
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", paramFile.length());
        return localContentValues;
    }
}
