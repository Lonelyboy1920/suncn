package com.gavin.giframe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.gavin.giframe.utils.GIFileUtil;
import com.gavin.giframe.utils.GILogUtil;
import com.gavin.giframe.utils.GIMyIntent;
import com.gavin.giframe.utils.GINotifyUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.utils.GIToastUtil;

import java.io.File;

public class GIDownloadService extends Service {
    private File tempFile; // 临时文件

    private Context context;
    private GINotifyUtil notify7;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notify7 = new GINotifyUtil(this, 7);
        Aria.download(this).register();
        context = this;
        Bundle bundle = null;
        if (intent != null)
            bundle = intent.getExtras();
        if (bundle != null) {
            String download_url = bundle.getString("url"); // 文件下载地址
            String fileName = bundle.getString("filename");  // 文件名称
            int smallIcon = bundle.getInt("smallIcon"); // 状态栏图标
            if (GIStringUtil.isNotEmpty(download_url) && GIStringUtil.isNotEmpty(fileName)) {
                GILogUtil.e("download_url==" + download_url);
                String path = GIFileUtil.createFilePath(context, fileName);
                GILogUtil.e("onStartCommand path==" + path);
                assert path != null;
                tempFile = new File(path);
                if (tempFile.exists() && path.contains(".apk")) {
                    tempFile.delete();
                }
                if (tempFile.exists()) {
                    doLogic();
                } else {
                    String tickerText = GIFileUtil.getFileName(fileName); // 状态栏弹框标题
                    notify7.init(null, smallIcon, tickerText + "下载中...", tickerText, "0%", false, false, false);
                    GIToastUtil.showMessage(this, tickerText + "开始下载...(在“通知栏”中可以查看下载进度)");
                    Aria.download(this)
                            .load(download_url)
                            .setFilePath(path, true)
                            .create();
                }
            }
        }
//        return super.onStartCommand(intent, Service.START_REDELIVER_INTENT, startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Aria.download(this).unRegister();
    }

    @Download.onNoSupportBreakPoint
    public void onNoSupportBreakPoint(DownloadTask task) {
        GILogUtil.i("该下载链接不支持断点");
    }

    @Download.onTaskStart
    public void onTaskStart(DownloadTask task) {
        GILogUtil.i(task.getDownloadEntity().getFileName() + "，开始下载");
    }

    @Download.onTaskStop
    public void onTaskStop(DownloadTask task) {
        GILogUtil.i(task.getDownloadEntity().getFileName() + "，停止下载");
    }

    @Download.onTaskCancel
    public void onTaskCancel(DownloadTask task) {
        GILogUtil.i(task.getDownloadEntity().getFileName() + "，取消下载");
    }

    @Download.onTaskFail
    public void onTaskFail(DownloadTask task) {
        GILogUtil.i(task.getDownloadEntity().getFileName() + "，下载失败");
    }

    @Download.onTaskComplete
    public void onTaskComplete(DownloadTask task) {
        GILogUtil.i(task.getDownloadEntity().getFileName() + "，下载完成");
        notify7.notify_progress(100, task.getDownloadEntity().getFilePath());
        doLogic();
    }

    @Download.onTaskRunning
    public void onTaskRunning(DownloadTask task) {
        long len = task.getFileSize();
        int p = (int) (task.getCurrentProgress() * 100 / len);
        notify7.notify_progress(p, "");
    }

    private void doLogic() {
        GIMyIntent.openFile(context, tempFile.getPath());
        stopSelf();
    }
}
