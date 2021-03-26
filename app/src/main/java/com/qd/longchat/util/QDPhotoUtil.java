package com.qd.longchat.util;

import android.database.Cursor;
import android.provider.MediaStore;

import com.qd.longchat.model.QDAlbum;
import com.qd.longchat.model.QDPhoto;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/8 下午5:42
 */
public class QDPhotoUtil {

    public static Map<String, QDAlbum> bucketMap = new HashMap<>();

    /**
     * 获取所有相册图片
     * @param cursor
     * @return
     */
    public static List<QDPhoto> initMobileAlbum(Cursor cursor) {
        bucketMap.clear();
        List<QDPhoto> photoList = new ArrayList<>();
        boolean hasMore = cursor.moveToFirst();
        while (hasMore) {
            QDPhoto photo = new QDPhoto();
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
            if (size <= 0) {
                hasMore = cursor.moveToNext();
                continue;
            }

            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            File file = new File(path);
            if (!file.exists() || file.length() <= 0) {
                hasMore = cursor.moveToNext();
                continue;
            }

            photo.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            photo.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
            photo.setSize(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)));

            String bucketId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
            QDAlbum album = bucketMap.get(bucketId);
            if (album == null) {
                album = new QDAlbum();
                List<QDPhoto> photos = new ArrayList<>();
                album.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)));
                album.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)));
                photos.add(photo);
                album.setPhotoList(photos);
                bucketMap.put(bucketId, album);
            } else {
                List<QDPhoto> photos = album.getPhotoList();
                photos.add(photo);
                album.setPhotoList(photos);
                bucketMap.put(bucketId, album);
            }
            photoList.add(photo);
            hasMore = cursor.moveToNext();
        }
        cursor.close();
        return photoList;
    }



}
