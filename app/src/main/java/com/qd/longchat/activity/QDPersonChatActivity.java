package com.qd.longchat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.longchat.base.QDClient;
import com.longchat.base.btf.IMBTFFile;
import com.longchat.base.btf.IMBTFManager;
import com.longchat.base.callback.QDFileCallBack;
import com.longchat.base.callback.QDMessageCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.callback.QDUserInfoCallBack;
import com.longchat.base.callback.QDUserStatusCallBack;
import com.longchat.base.config.QDSDKConfig;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.manager.listener.QDMessageCallBackManager;
import com.longchat.base.manager.listener.QDUserInfoCallBackManager;
import com.longchat.base.manager.listener.QDUserStatusCallBackManager;
import com.longchat.base.model.gd.QDFileModel;
import com.longchat.base.util.QDBadWordUtil;
import com.longchat.base.util.QDLog;
import com.qd.longchat.R;
import com.qd.longchat.adapter.QDChatAdapter;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.config.QDRolAce;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.widget.QDChatSmiley;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindString;
import butterknife.OnClick;

import com.qd.longchat.R2;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/4 上午9:39
 */
public class QDPersonChatActivity extends QDChatActivity implements QDMessageCallBack, QDUserStatusCallBack, QDUserInfoCallBack {

    @BindString(R2.string.chat_my_pc)
    String strMyPc;
    @BindString(R2.string.open_secret)
    String strOpenSecret;
    @BindString(R2.string.close_secret)
    String strCloseSecret;

    private boolean isToPc;

    private boolean isRegister;

    private Handler handler;

    private List<Long> secretReadTimeList; //密聊过期时间列表

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            QDMessage message = intent.getParcelableExtra(QDIntentKeyUtil.INTENT_KEY_MESSAGE);
            adapter.addMsg(message);
        }
    };

    private QDChatAdapter.OnResendListener resendListener = new QDChatAdapter.OnResendListener() {
        @Override
        public void onResend(final QDMessage message) {
            QDResultCallBack callBack = new QDResultCallBack() {
                @Override
                public void onError(String errorMsg) {
                    updateMessageStatus(adapter.getMsgList(), message.getMsgId(), QDMessage.MSG_STATUS_SEND_FAILED);
                }

                @Override
                public void onSuccess(Object o) {
                    updateMessageStatus(adapter.getMsgList(), message.getMsgId(), QDMessage.MSG_STATUS_SEND_OK);
                }
            };


            String filePath = message.getFilePath();
            if (TextUtils.isEmpty(filePath)) {
                QDClient.getInstance().resendPersonMessage(message, callBack);
            } else {
                int fileStatus = message.getFileStatus();
                if (fileStatus != QDMessage.MSG_FILE_STATUS_UPLOAD_FAILED) {
                    QDClient.getInstance().resendPersonMessage(message, callBack);
                } else {
                    File file = new File(filePath);
                    QDClient.getInstance().uploadFile(message.getMsgId(), file, "file", new QDFileCallBack<QDFileModel>() {
                        @Override
                        public void onUploading(String filePath, int per) {
                            QDLog.e("QDChatActivity", "file upload per is: " + per);
                        }

                        @Override
                        public void onUploadFailed(String errorMsg) {
                            QDLog.e("QDChatActivity", "file upload failed: " + errorMsg);
                            updateMessageFileStatus(adapter.getMsgList(), message.getMsgId(), QDMessage.MSG_FILE_STATUS_UPLOAD_FAILED);
                        }

                        @Override
                        public void onUploadSuccess(QDFileModel model) {
                            String path = message.getFilePath();
                            File mFile = new File(path);
                            String md5 = QDUtil.getFileMd5(mFile);
                            message.setFileStatus(QDMessage.MSG_FILE_STATUS_UPLOADED);
                            String id = model.getFileId().toLowerCase();
                            String fsHost = QDUtil.getWebFileServer();
                            String fsHost1 = QDUtil.getFileServer();
                            message.setAttachment(model.getName() + "/" + model.getSize() + "/" + id);
                            IMBTFManager manager = new IMBTFManager();
                            IMBTFFile imbtfFile = new IMBTFFile(model.getName(), model.getSize(), message.getMsgType(), md5, fsHost, model.getOriginal(), model.getOriginal(), model.getName() + ";" + id, fsHost1);
                            manager.addItem(imbtfFile);
                            message.setContent(manager.toBTFXml());
                            QDMessageHelper.updateMessage(message);
                            QDClient.getInstance().sendMessage(user, message, new QDResultCallBack() {
                                @Override
                                public void onError(String errorMsg) {
                                    updateMessageStatus(adapter.getMsgList(), message.getMsgId(), QDMessage.MSG_STATUS_SEND_FAILED);
                                }

                                @Override
                                public void onSuccess(Object o) {
                                    updateMessageStatus(adapter.getMsgList(), message.getMsgId(), QDMessage.MSG_STATUS_SEND_OK);
                                }
                            });

                        }
                    });
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isGroup = false;
        super.onCreate(savedInstanceState);
        handler = new Handler();
        secretReadTimeList = new ArrayList<>();
        user = QDUserHelper.getUserById(chatId);
        if (user == null) {
            QDClient.getInstance().fetchUserInfo(chatId);
            QDUserInfoCallBackManager.getInstance().addCallBack(this);
        } else {
            initData();
        }
        secretStatus = QDClient.getInstance().getUserSecret(chatId);
        setSecretVisible();
    }

    private void initData() {
        if (chatId.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
            tvTitleName.setText(strMyPc);
            isToPc = true;
            btnVoice.setVisibility(View.GONE);
            ivSmile.setVisibility(View.GONE);
        } else {
            isToPc = false;
            tvTitleName.setText(user.getName());
            tvTitleRight.setVisibility(View.VISIBLE);
            tvTitleRight.setBackgroundResource(R.drawable.ic_user_detail);
            tvTitleSunname.setVisibility(View.VISIBLE);
            tvTitleSunname.setText(QDUtil.getUserStatus(context, chatId));
        }
        initMessageData();
        refreshAndLoad();
        registerBroadcast();
        readDraft();
        QDClient.getInstance().subscribeSecret(chatId);
        QDMessageCallBackManager.getInstance().addCallBack(this);
        QDUserStatusCallBackManager.getInstatnce().addCallBack(this);
    }

    private void setSecretVisible() {
        if (isToPc) {
            return;
        }
        if (QDUtil.isHaveClientAce(QDRolAce.ACE_CLIENT_OPEN_SECRET_CHAT)) {
            tvTitleFun.setVisibility(View.VISIBLE);
            if (secretStatus == 0) {
                tvTitleFun.setBackgroundResource(R.drawable.ic_burn_close);
            } else {
                tvTitleFun.setBackgroundResource(R.drawable.ic_burn_open);
            }
        } else {
            secretStatus = 0;
            tvTitleFun.setVisibility(View.GONE);
        }
        initFunc(initFunIds());
    }

    private void registerBroadcast() {
        if (!isRegister) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(QDAVActivity.ACTION_ON_AV_END);
            registerReceiver(receiver, filter);
            isRegister = true;
        }
    }

    private void unregisterBroadcast() {
        if (isRegister) {
            unregisterReceiver(receiver);
            isRegister = false;
        }
    }

    private void refreshAndLoad() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                long time;
                if (adapter.getMsgList().size() == 0) {
                    time = System.currentTimeMillis() * 1000;
                } else {
                    time = adapter.getMsgList().get(0).getCreateDate();
                }
                List<QDMessage> messageList;
                if (isToPc) {
                    messageList = QDMessageHelper.getMessageLimitWithSelfIdBeforeTime(chatId, time);
                } else {
                    messageList = QDMessageHelper.getMessageLimitWithUserIdBeforeTime(chatId, time);
                }
                if (messageList != null && messageList.size() > 0) {
                    msgList.addAll(0, messageList);
                    adapter.setMsgList(msgList);
                }

                if (messageList == null || messageList.size() < 30) {
                    refreshLayout.setEnableRefresh(false);
                }
                refreshLayout.finishRefresh();
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                long time;
                if (adapter.getMsgList().size() == 0) {
                    time = System.currentTimeMillis() * 1000;
                } else {
                    time = adapter.getMsgList().get(adapter.getCount() - 1).getCreateDate();
                }
                List<QDMessage> messageList = QDMessageHelper.getMessageLimitWithUserIdAfterTime(chatId, time + 1);
                if (messageList != null && messageList.size() > 0) {
                    msgList.addAll(messageList);
                    adapter.addMsg(messageList);
                }
                refreshLayout.finishLoadMore(500);
                if (messageList == null || messageList.size() < 30) {
                    refreshLayout.setEnableLoadMore(false);
                }
            }
        });

    }

    private void initMessageData() {
        deleteSecretMessage();
        int selection;
        if (mMsgTime == 0) {
            if (isToPc) {
                msgList = QDMessageHelper.getMessageLimitWithSelfId(chatId);
            } else {
                msgList = QDMessageHelper.getMessageLimitWithUserId(chatId);
            }
            selection = msgList.size();
        } else {
            msgList = new ArrayList<>();
            List<QDMessage> beforeList = QDMessageHelper.getMessageLimitWithUserIdBeforeTime(chatId, mMsgTime, 4);
            List<QDMessage> afterList = QDMessageHelper.getMessageLimitWithUserIdAfterTime(chatId, mMsgTime, 10);
            msgList.addAll(beforeList);
            msgList.addAll(afterList);
            selection = beforeList.size() + 1;
            if (beforeList.size() < 4) {
                refreshLayout.setEnableRefresh(false);
            } else {
                refreshLayout.setEnableRefresh(true);
            }
            if (afterList.size() < 10) {
                refreshLayout.setEnableLoadMore(false);
            } else {
                refreshLayout.setEnableLoadMore(true);
            }
        }
        adapter.setListener(listener);
        adapter.setResendListener(resendListener);
        adapter.setMsgList(msgList);
        setMsgRead();
        scrollListViewToSelection(selection);
    }

    private void setMsgRead() {
        QDMessage unreadMessage = null;
        Collections.reverse(msgList);
        for (QDMessage message : msgList) {
            if (message.getIsRead() == QDMessage.MSG_UNREAD || message.getIsRead() == QDMessage.MSG_IGNORE) {
                unreadMessage = message;
                break;
            }
        }
        Collections.reverse(msgList);
        if (unreadMessage != null) {
            QDClient.getInstance().setMsgRead(chatId, unreadMessage);
        }
        if (isToPc) {
            QDMessageHelper.setMessageReadWithSelfId(chatId);
        } else {
            long time = System.currentTimeMillis() + QDClient.getInstance().getLoginInfo().getSTimeOffset();
            for (QDMessage message : msgList) {
                if (message.getDirection() == QDMessage.DIRECTION_IN && message.getIsRead() == QDMessage.MSG_UNREAD) {
                    message.setReadTime(time * 1000);
                }
            }
            QDMessageHelper.setMessageReadWithUserId(time * 1000, chatId);
        }
        getSecretReadTimeList(msgList);
    }

    /**
     * 删除过期的密聊消息
     */
    private void deleteSecretMessage() {
        long time = System.currentTimeMillis() + QDClient.getInstance().getLoginInfo().getSTimeOffset() - SECRET_TIME_INTERVAL;
        QDMessageHelper.deleteTimeOutSecretMessage(time * 1000);
    }

    private void getSecretReadTimeList(List<QDMessage> messageList) {
        handler.removeCallbacks(runnable);
        secretReadTimeList.clear();
        for (QDMessage message : messageList) {
            if (message.getMsgFlag() == QDMessage.MSGFLAG_SECRET) {
                int direction = message.getDirection();
                if (direction == QDMessage.DIRECTION_OUT && message.getStatus() == QDMessage.MSG_STATUS_READ_ACK) {
                    long readTime = message.getReadTime();
                    if (!secretReadTimeList.contains(readTime)) {
                        secretReadTimeList.add(readTime);
                    }
                } else if (direction == QDMessage.DIRECTION_IN) {
                    long readTime = message.getReadTime();
                    if (!secretReadTimeList.contains(readTime)) {
                        secretReadTimeList.add(readTime);
                    }
                }
            }
        }
        if (secretReadTimeList.size() != 0) {
            operateDelayed();
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            deleteSecretMessage();
            List<QDMessage> msgList = adapter.getMsgList();
            List<QDMessage> messageList = new ArrayList<>();
            for (QDMessage message : msgList) {
                if (message.getMsgFlag() == QDMessage.MSGFLAG_SECRET) {
                    int direction = message.getDirection();
                    long time = System.currentTimeMillis() + QDClient.getInstance().getLoginInfo().getSTimeOffset() - SECRET_TIME_INTERVAL;
                    if (direction == QDMessage.DIRECTION_OUT && message.getStatus() == QDMessage.MSG_STATUS_READ_ACK) {
                        long readTime = message.getReadTime() / 1000;
                        if (readTime > time) {
                            messageList.add(message);
                        }
                    } else if (direction == QDMessage.DIRECTION_IN && message.getReadTime() != 0) {
                        long readTime = message.getReadTime() / 1000;
                        if (readTime > time) {
                            messageList.add(message);
                        }
                    } else {
                        messageList.add(message);
                    }
                } else {
                    messageList.add(message);
                }
            }
            secretReadTimeList.remove(0);
            adapter.setMsgList(messageList);
            if (secretReadTimeList.size() != 0) {
                operateDelayed();
            }
        }
    };

    private void operateDelayed() {
        long time = secretReadTimeList.get(0);
        long currentTime = System.currentTimeMillis() + QDClient.getInstance().getLoginInfo().getSTimeOffset();
        long delayed = SECRET_TIME_INTERVAL - (currentTime - time / 1000);
        handler.postDelayed(runnable, delayed);
    }

    @Override
    protected void doSendFile(File file, String subject, final String type) {
        final String md5 = QDUtil.getFileMd5(file);
        long attachSize = QDLanderInfo.getInstance().getAttachSizeLimit();
        long fileSize = file.length() / (1024 * 1024);
        if (attachSize != 0 && fileSize > attachSize) {
            QDUtil.showToast(context, strFileSizeBeyond);
            return;
        }
        final QDMessage message = QDClient.getInstance().createFileMessage(file, user, subject, type, isSecret());
        if (secretStatus == 1) {
            message.setMsgFlag(QDMessage.MSGFLAG_SECRET);
        }
        if (!com.longchat.base.util.QDUtil.isNetworkAvailable(context)) {
            message.setFileStatus(QDMessage.MSG_FILE_STATUS_UPLOAD_FAILED);
            message.setStatus(QDMessage.MSG_STATUS_SEND_FAILED);
            adapter.addMsg(message);
            return;
        }
        adapter.addMsg(message);
        scrollListViewToSelection(adapter.getCount() - 1);
        QDClient.getInstance().uploadFile(message.getMsgId(), file, "file", new QDFileCallBack<QDFileModel>() {
            @Override
            public void onUploading(String filePath, int per) {
                QDLog.e("QDChatActivity", "file upload per is: " + per);
            }

            @Override
            public void onUploadFailed(String errorMsg) {
                QDLog.e("QDChatActivity", "file upload failed: " + errorMsg);
                updateMessageFileStatus(adapter.getMsgList(), message.getMsgId(), QDMessage.MSG_FILE_STATUS_UPLOAD_FAILED);
            }

            @Override
            public void onUploadSuccess(QDFileModel model) {
                QDLog.e("1111", "the model is: " + model.getId());
                message.setFileStatus(QDMessage.MSG_FILE_STATUS_UPLOADED);
                String id = model.getFileId().toLowerCase();
                String fsHost = QDUtil.getWebFileServer();
                String fsHost1 = QDUtil.getFileServer();
                message.setAttachment(model.getName() + "/" + model.getSize() + "/" + id);
                IMBTFManager manager = new IMBTFManager();
                IMBTFFile imbtfFile = new IMBTFFile(model.getName(), model.getSize(), type, md5, fsHost, model.getOriginal(), model.getOriginal(), model.getName() + ";" + id, fsHost1);
                manager.addItem(imbtfFile);
                message.setContent(manager.toBTFXml());
                QDMessageHelper.updateMessage(message);
                QDClient.getInstance().sendMessage(user, message, new QDResultCallBack() {
                    @Override
                    public void onError(String errorMsg) {
                        updateMessageStatus(adapter.getMsgList(), message.getMsgId(), QDMessage.MSG_STATUS_SEND_FAILED);
                    }

                    @Override
                    public void onSuccess(Object o) {
                        updateMessageStatus(adapter.getMsgList(), message.getMsgId(), QDMessage.MSG_STATUS_SEND_OK);
                    }
                });

            }
        });
    }

    @Override
    protected void doSendLocationMessage(String locInfo) {
        QDMessage message = QDClient.getInstance().sendLocationMessage(user, locInfo, isSecret(), new QDResultCallBack<String>() {
            @Override
            public void onError(String msgId) {
                updateMessageStatus(adapter.getMsgList(), msgId, QDMessage.MSG_STATUS_SEND_FAILED);
            }

            @Override
            public void onSuccess(String msgId) {
                updateMessageStatus(adapter.getMsgList(), msgId, QDMessage.MSG_STATUS_SEND_OK);
            }
        });
        adapter.addMsg(message);
        scrollListViewToBottom();
    }

    @Override
    protected void doSendCard(QDUser user) {
        final QDMessage message = QDClient.getInstance().createCardMessage(user, this.user, isSecret());
        if (secretStatus == 1) {
            message.setMsgFlag(QDMessage.MSGFLAG_SECRET);
        }
        adapter.addMsg(message);
        scrollListViewToBottom();
        QDClient.getInstance().sendMessage(this.user, message, new QDResultCallBack() {
            @Override
            public void onError(String errorMsg) {
                updateMessageStatus(adapter.getMsgList(), message.getMsgId(), QDMessage.MSG_STATUS_SEND_FAILED);
            }

            @Override
            public void onSuccess(Object o) {
                updateMessageStatus(adapter.getMsgList(), message.getMsgId(), QDMessage.MSG_STATUS_SEND_OK);
            }
        });
    }

    @OnClick({R2.id.tv_title_right, R2.id.btn_chat_send, R2.id.tv_title_fun})
    public void click(View view) {
        int i = view.getId();
        if (i == R.id.tv_title_right) {
            Intent intent = new Intent(context, QDPersonInfoActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_USER_ID, user.getId());
            startActivityForResult(intent, REQUEST_CLEAR_HISTORY);

        } else if (i == R.id.btn_chat_send) {
            String content = etInput.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                return;
            }

            if (QDSDKConfig.getInstance().getIsForbidden()) {
                int forbiddenType = QDSDKConfig.getInstance().getForbiddenType();
                long offset = System.currentTimeMillis() - QDSDKConfig.getInstance().getLastBadTime();
                if (forbiddenType == 0) {
                    if (offset > BAD_TIME_INTERVAL) {
                        QDSDKConfig.getInstance().setIsForbidden(false);
                    } else {
                        int index = (int) (((BAD_TIME_INTERVAL - offset) / 1000) / 60);
                        if ((((BAD_TIME_INTERVAL - offset) / 1000) % 60) != 0) {
                            index += 1;
                        }
                        warnDialog.setContent(String.format(strAceForbidden, index));
                        warnDialog.show();
                        return;
                    }
                } else {
                    if (offset > BAD_TIME_INTERVAL_1) {
                        QDSDKConfig.getInstance().setIsForbidden(false);
                    } else {
                        int index = (int) (((BAD_TIME_INTERVAL_1 - offset) / 1000) / 60);
                        if ((((BAD_TIME_INTERVAL_1 - offset) / 1000) % 60) != 0) {
                            index += 1;
                        }
                        warnDialog.setContent(String.format(strAceForbidden, index));
                        warnDialog.show();
                        return;
                    }
                }
            }

            String content1 = QDChatSmiley.getInstance(context).strToSmileyInfo(content);

            if (isOpenBadWord && QDBadWordUtil.isContainBadWord(content1)) {
                if (alarmType == 1) {
                    QDClient.getInstance().sendBadWordAlarm(getBadData(content));
                }
                showBadTimeDialog();
                return;
            }

            QDMessage message = QDClient.getInstance().createPersonTextMessage(user, content, isSecret(), isSignOpen);
            message.setSubject(QDChatSmiley.getInstance(context).strToSmileyInfo(message.getSubject()));
            QDClient.getInstance().resendPersonMessage(message, new QDResultCallBack<String>() {
                @Override
                public void onError(String msgId) {
                    updateMessageStatus(adapter.getMsgList(), msgId, QDMessage.MSG_STATUS_SEND_FAILED);
                }

                @Override
                public void onSuccess(String msgId) {
                    updateMessageStatus(adapter.getMsgList(), msgId, QDMessage.MSG_STATUS_SEND_OK);
                }
            });

            adapter.addMsg(message);
            etInput.setText("");
            scrollListViewToBottom();

        } else if (i == R.id.tv_title_fun) {
            String subject;
            if (secretStatus == 0) {
                secretStatus = 1;
                subject = strOpenSecret;
            } else {
                secretStatus = 0;
                subject = strCloseSecret;
            }
            adapter.addMsg(QDClient.getInstance().createNotifyMessage(user, subject));
            QDClient.getInstance().openOrCloseSecret(chatId, user.getName(), secretStatus);
            setSecretVisible();

        }
    }

    private boolean isSecret() {
        boolean isSecret;
        if (secretStatus == 0) {
            isSecret = false;
        } else {
            isSecret = true;
        }
        return isSecret;
    }

    @Override
    protected void onPause() {
        super.onPause();
        QDMessageHelper.setMessageReadWithUserId(chatId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
        QDMessageCallBackManager.getInstance().removeLast();
        QDUserStatusCallBackManager.getInstatnce().removeLast();
        QDUserInfoCallBackManager.getInstance().removeCallBack(this);
    }

    @Override
    public void onReceive(List<QDMessage> messages) {
        List<QDMessage> messageList = new ArrayList<>();
        List<QDMessage> secretList = new ArrayList<>();
        for (QDMessage message : messages) {
            int ctype = message.getCtype();
            if (ctype == QDMessage.CTYPE_USER) {
                String senderId = message.getSenderId();
                String receiverId = message.getReceiverId();
                if (isToPc) {
                    if (senderId.equalsIgnoreCase(receiverId) && senderId.equalsIgnoreCase(chatId)) {
                        messageList.add(message);
                    }
                } else {
                    if (!senderId.equalsIgnoreCase(receiverId) && (senderId.equalsIgnoreCase(chatId) || receiverId.equalsIgnoreCase(chatId))) {
                        if (message.getMsgFlag() == QDMessage.MSGFLAG_SECRET && message.getDirection() == QDMessage.DIRECTION_IN) {
                            long time = System.currentTimeMillis() + QDClient.getInstance().getLoginInfo().getSTimeOffset();
                            message.setReadTime(time * 1000);
                            secretList.add(message);
                        }
                        messageList.add(message);
                    }
                }

            }
        }

        if (messageList.size() > 0) {
            QDClient.getInstance().setMsgRead(chatId, messageList.get(messageList.size() - 1));
        }
        if (secretList.size() > 0) {
            QDMessageHelper.insertMessageList(secretList);
        }
        adapter.addMsg(messageList);
        scrollListViewToBottom();
        getSecretReadTimeList(adapter.getMsgList());
    }

    @Override
    public void onReceiveGMsg(String groupId, List<QDMessage> messageList) {

    }

    @Override
    public void onMsgReaded(String userId) {
        if (userId.equalsIgnoreCase(chatId)) {
            if (adapter.getMsgList().size() > 0) {
                long time = adapter.getMsgList().get(0).getCreateDate();
                if (isToPc) {
                    msgList = QDMessageHelper.getMessageWithSelfIdAfterTime(chatId, time);
                } else {
                    msgList = QDMessageHelper.getMessageWithUserIdAfterTime(chatId, time);
                }
            }
            adapter.setMsgList(msgList);
            getSecretReadTimeList(msgList);
        }
    }

    @Override
    public void onRevokeMessage(String msgId, String errorMsg) {
        if (TextUtils.isEmpty(errorMsg)) {
            List<QDMessage> messageList = new ArrayList<>();
            messageList.addAll(adapter.getMsgList());
            int size = messageList.size();
            for (int i = 0; i < size; i++) {
                QDMessage message = messageList.get(i);
                if (message.getMsgId().equalsIgnoreCase(msgId)) {
                    messageList.remove(i);
                    messageList.add(i, QDMessageHelper.getMessageById(msgId));
                    break;
                }
            }
            adapter.setMsgList(messageList);
        } else {
            QDUtil.showToast(context, errorMsg);
        }
    }


    @Override
    public void onDeleteMessage(String groupId, String msgId) {
        if (chatId.equalsIgnoreCase(groupId)) {
            adapter.removeMsg(msgId);
        }
    }

    @Override
    public void onUserStatusChange(String userId, int status) {
        if (chatId.equalsIgnoreCase(userId)) {
            tvTitleSunname.setText(QDUtil.getUserStatus(context, userId));
        }
    }

    @Override
    public void onUserInfoChange(QDUser user) {
        if (chatId.equalsIgnoreCase(user.getId())) {
            this.user = user;
            initData();
        }
    }

    @Override
    public void onUserSecretChange(String userId, int status) {
        if (userId.equalsIgnoreCase(chatId)) {
            secretStatus = status;
            setSecretVisible();
        }
    }

    @Override
    public void onFetchUserInfo(String userId) {
        if (userId.equalsIgnoreCase(chatId)) {
            user = QDUserHelper.getUserById(userId);
            initData();
        }
    }
}
