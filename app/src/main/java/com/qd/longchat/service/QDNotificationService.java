package com.qd.longchat.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.longchat.base.callback.QDMessageCallBack;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.manager.listener.QDMessageCallBackManager;
import com.suncn.ihold_zxztc.MyApplication;
import com.suncn.ihold_zxztc.activity.MainActivity;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/8/14 下午2:14
 */
public class QDNotificationService extends Service implements QDMessageCallBack {
    private NotificationManager manager;
    private NotificationCompat.Builder builder;
    private MyApplication mApplication;
    private int personNum;
    private int groupNum;

    /**
     * 通知栏消息服务广播接收者
     */
    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.qd.longchat.notify".equals(intent.getAction())) {
                cancelNotification();
            }
        }
    };
    private PendingIntent pendingIntent;

    /**
     * 清除通知
     */
    protected void cancelNotification() {
        personNum = 0;
        groupNum = 0;
        manager.cancel(1);
        manager.cancel(2);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = (MyApplication) getApplication();
        QDMessageCallBackManager.getInstance().addCallBack(this);
        initNotificationManager();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.qd.longchat.notify");
        registerReceiver(notificationReceiver, filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initNotificationManager() {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "chat";
            String channelName = "聊天消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, channelId);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
    }

    public void sendMsg(int chatId, String title, String content) {
        Notification notification = builder
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(com.qd.longchat.R.mipmap.ic_launcher)
                .setAutoCancel(true)
                //设置悬浮
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
                .setFullScreenIntent(pendingIntent, true)
                .build();
        manager.notify(chatId, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationReceiver);
    }

    @Override
    public void onReceive(List<QDMessage> message) {
        if (mApplication.getRefCount() == 0) {
            personNum = QDMessageHelper.getPersonUnreadMessageCount();
            if (personNum != 0) {
                sendMsg(1, "聊天消息", "您有" + personNum + "条新消息");
            }
        }
    }

    @Override
    public void onReceiveGMsg(String groupId, List<QDMessage> messageList) {
        if (mApplication.getRefCount() == 0) {
            groupNum = QDMessageHelper.getGroupUnreadMessageCount();
            if (groupNum != 0) {
                sendMsg(2, "聊天消息", "您有" + groupNum + "条群组消息");
            }
        }
    }

    @Override
    public void onMsgReaded(String userId) {

    }

    @Override
    public void onRevokeMessage(String msgId, String errorMsg) {

    }

    @Override
    public void onDeleteMessage(String groupId, String msgId) {

    }
}
