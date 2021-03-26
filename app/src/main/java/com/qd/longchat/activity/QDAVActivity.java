package com.qd.longchat.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.QDAudioActivity;
import com.gavin.giframe.utils.GISharedPreUtil;
import com.google.gson.JsonObject;
import com.longchat.base.QDClient;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.model.QDAVCmd;
import com.longchat.base.util.QDGson;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDHttpUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;


import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/8/22 上午10:03
 */
public class QDAVActivity extends QDBaseActivity {

    public final static String ACTION_ON_AV_END = "com.qd.longchat.avend";

    @BindView(R2.id.iv_av_icon)
    ImageView ivIcon;
    @BindView(R2.id.tv_av_name)
    TextView tvName;
    @BindView(R2.id.fl_av_bottom_layout)
    FrameLayout flBottomLayout;

    @BindString(R2.string.cancel)
    String strCancel;
    @BindString(R2.string.av_had_cancel)
    String strHadCancel;
    @BindString(R2.string.av_had_refuse)
    String strHadRefuse;
    @BindString(R2.string.av_had_busy)
    String strHadBusy;
    @BindString(R2.string.av_initiate_voice_request)
    String strInitiateVoiceRequest;
    @BindString(R2.string.av_initiate_video_request)
    String strInitiateVideoRequest;
    @BindString(R2.string.friend_invite_agree)
    String strAgree;
    @BindString(R2.string.friend_invite_refuse)
    String strRefuse;
    @BindString(R2.string.friend_invite_had_refuse)
    String strSelfRefuse;
    @BindString(R2.string.av_self_cancel)
    String strSelfCancel;
    @BindString(R2.string.av_cancel_voice_request)
    String strCancelVoiceRequest;
    @BindString(R2.string.av_cancel_video_request)
    String strCancelVideoRequest;

    private boolean isOut; //是否是自己发出的
    private String userId;
    private QDUser user;
    private QDAVCmd cmd;
    private String roomId;
    private String avUrl;
    private boolean isAudio;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            QDAVCmd avcmd = intent.getParcelableExtra("AvCmd");
            if (action.equalsIgnoreCase("com.qd.longchat.avcall")) {
                if (avcmd.getCmdName().equalsIgnoreCase(QDAVCmd.CMD_NAME_VIDEO_END)) {
                    String content = strHadCancel;
                    QDMessage message = QDClient.getInstance().createAVMessage(userId, user.getName(), content, true, false);
                    finishActivity(message);
                } else if (avcmd.getCmdName().equalsIgnoreCase(QDAVCmd.CMD_NAME_AUDIO_END)) {
                    String content = strHadCancel;
                    QDMessage message = QDClient.getInstance().createAVMessage(userId, user.getName(), content, false, false);
                    finishActivity(message);
                }
            } else if (action.equalsIgnoreCase("com.qd.longchat.avresponse")) {
                int nReturn = avcmd.getnReturn();
                if (nReturn == QDAVCmd.CMD_REPLY_AGREE) {
                    toIntent(true);
                } else if (nReturn == QDAVCmd.CMD_REPLY_REFUSE) {
                    String content = strHadRefuse;
                    QDMessage message;
                    if (avcmd.getCmdName().equalsIgnoreCase(QDAVCmd.CMD_NAME_AUDIO_REQ)) {
                        message = QDClient.getInstance().createAVMessage(userId, user.getName(), content, false, true);
                        finishActivity(message);
                    } else if (avcmd.getCmdName().equalsIgnoreCase(QDAVCmd.CMD_NAME_VIDEO_REQ)) {
                        message = QDClient.getInstance().createAVMessage(userId, user.getName(), content, true, true);
                        finishActivity(message);
                    }
                } else if (nReturn == QDAVCmd.CMD_REPLY_BUSY) {
                    String content = strHadBusy;
                    QDMessage message;
                    if (avcmd.getCmdName().equalsIgnoreCase(QDAVCmd.CMD_NAME_AUDIO_REQ)) {
                        message = QDClient.getInstance().createAVMessage(userId, user.getName(), content, false, true);
                        finishActivity(message);
                    } else if (avcmd.getCmdName().equalsIgnoreCase(QDAVCmd.CMD_NAME_VIDEO_REQ)) {
                        message = QDClient.getInstance().createAVMessage(userId, user.getName(), content, true, true);
                        finishActivity(message);
                    }
                }
            } else if (action.equalsIgnoreCase("com.qd.longchat.avfinish")) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        setContentView(R.layout.activity_av);
        GISharedPreUtil.setValue(activity,"isInCall",true);
        ButterKnife.bind(this);

        cmd = getIntent().getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_AV_CMD);
        isOut = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_AV_IS_OUT, false);
        userId = getIntent().getStringExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID);
        isAudio = getIntent().getBooleanExtra(QDIntentKeyUtil.INTENT_KEY_IS_AUDIO, false);
        initUI();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.qd.longchat.avcall");
        filter.addAction("com.qd.longchat.avresponse");
        filter.addAction("com.qd.longchat.avfinish");
        registerReceiver(receiver, filter);
    }

    private void initUI() {
        user = QDUserHelper.getUserById(userId);
        QDBitmapUtil.getInstance().createAvatar(context, userId, user.getName(), user.getPic(), ivIcon);
        tvName.setText(user.getName());
        initBottomLayout();
    }

    private void getRoomId() {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String url = QDUtil.getWebApiServer() + "/api/media/create_room.html";
                int roomType;
                if (isAudio) {
                    roomType = 1;
                } else {
                    roomType = 2;
                }
                String result = QDHttpUtil.sendGet(url, roomType);
                return result;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JsonObject jsonObject = QDGson.getGson().fromJson(s, JsonObject.class);
                int status = jsonObject.get("status").getAsInt();
                if (status == 1) {
                    JsonObject data = jsonObject.get("data").getAsJsonObject();
                    avUrl = data.toString();
                    String subject;
                    if (isAudio) {
                        subject = QDLanderInfo.getInstance().getName() + strInitiateVoiceRequest;
                        cmd = QDClient.getInstance().doAVCall(user, false, subject, avUrl);
                    } else {
                        subject = QDLanderInfo.getInstance().getName() + strInitiateVideoRequest;
                        cmd = QDClient.getInstance().doAVCall(user, true, subject, avUrl);
                    }
                } else {
                    QDUtil.showToast(context, jsonObject.get("msg").getAsString());
                    finish();
                }
            }
        };
        task.execute();
    }

    private void initBottomLayout() {
        if (isOut) {
            createOutBottomLayout();
            getRoomId();
        } else {
            avUrl = cmd.getBody();
            createInBottomLayout();
        }
    }

    private void createOutBottomLayout() {
        View view = LayoutInflater.from(context).inflate(R.layout.item_av_call_layout, null);
        ImageView ivHuangUp = view.findViewById(R.id.iv_av_hang_up);
        TextView tvHangUp = view.findViewById(R.id.tv_av_hang_up_text);
        tvHangUp.setText(strCancel);
        flBottomLayout.addView(view);
        ivHuangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject;
                QDMessage message;
                String content = strSelfCancel;
                if (isAudio) {
                    subject = QDLanderInfo.getInstance().getName() + strCancelVoiceRequest;
                    if (cmd != null)
                        QDClient.getInstance().doAVHangUp(cmd.getGuid(), user, false, subject);
                    message = QDClient.getInstance().createAVMessage(userId, user.getName(), content, false, true);
                } else {
                    subject = QDLanderInfo.getInstance().getName() + strCancelVideoRequest;
                    if (cmd != null)
                        QDClient.getInstance().doAVHangUp(cmd.getGuid(), user, true, subject);
                    message = QDClient.getInstance().createAVMessage(userId, user.getName(), content, true, true);
                }
                finishActivity(message);
            }
        });
    }

    private void createInBottomLayout() {
        View view = LayoutInflater.from(context).inflate(R.layout.item_av_in_layout, null);
        ImageView ivAgree = view.findViewById(R.id.iv_av_agree_icon);
        ImageView ivRefuse = view.findViewById(R.id.iv_av_refuse_icon);
        flBottomLayout.addView(view);

        ivAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudio) {
                    QDClient.getInstance().replyAVCall(cmd, user, false, strAgree, QDAVCmd.CMD_REPLY_AGREE);
                } else {
                    QDClient.getInstance().replyAVCall(cmd, user, true, strAgree, QDAVCmd.CMD_REPLY_AGREE);
                }
                toIntent(false);
            }
        });

        ivRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QDMessage message;
                String content = strSelfRefuse;
                if (isAudio) {
                    QDClient.getInstance().replyAVCall(cmd, user, false, strRefuse, QDAVCmd.CMD_REPLY_REFUSE);
                    message = QDClient.getInstance().createAVMessage(userId, user.getName(), content, false, false);
                } else {
                    QDClient.getInstance().replyAVCall(cmd, user, true, strAgree, QDAVCmd.CMD_REPLY_REFUSE);
                    message = QDClient.getInstance().createAVMessage(userId, user.getName(), content, true, false);
                }
                finishActivity(message);
            }
        });
    }

    private void toIntent(boolean isOut) {
        Intent intent = new Intent(context, QDAudioActivity.class);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER, user);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_ROOM_ID, avUrl);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_OUT, isOut);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_IS_AUDIO, isAudio);
        startActivity(intent);
        finish();
    }

    private void finishActivity(QDMessage message) {
        GISharedPreUtil.setValue(activity,"isInCall",false);
        Intent intent = new Intent(ACTION_ON_AV_END);
        intent.putExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE, message);
        sendBroadcast(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
