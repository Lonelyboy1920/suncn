package com.gavin.giframe.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gavin.giframe.utils.GIMyIntent;

/**
 * 下载成功后点击通知栏打开文件
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String filePath = intent.getStringExtra("filePath");
        if ("notification_clicked".equals(action)) {
            GIMyIntent.openFile(context, filePath);
        }
    }
}
