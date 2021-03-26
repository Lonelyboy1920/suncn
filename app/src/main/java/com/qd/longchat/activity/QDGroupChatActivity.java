package com.qd.longchat.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.longchat.base.QDClient;
import com.longchat.base.btf.IMBTFAT;
import com.longchat.base.btf.IMBTFFile;
import com.longchat.base.btf.IMBTFManager;
import com.longchat.base.btf.IMBTFText;
import com.longchat.base.callback.QDFileCallBack;
import com.longchat.base.callback.QDGroupCallBack;
import com.longchat.base.callback.QDMessageCallBack;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.config.QDSDKConfig;
import com.longchat.base.dao.QDGroup;
import com.longchat.base.dao.QDGroupMember;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDGroupHelper;
import com.longchat.base.databases.QDGroupMemberHelper;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.manager.listener.QDGroupCallBackManager;
import com.longchat.base.manager.listener.QDMessageCallBackManager;
import com.longchat.base.model.gd.QDFileModel;
import com.longchat.base.util.QDBadWordUtil;
import com.longchat.base.util.QDLog;
import com.qd.longchat.R;
import com.qd.longchat.R2;
import com.qd.longchat.adapter.QDChatAdapter;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.util.QDAtClickSpan;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;
import com.qd.longchat.widget.QDChatSmiley;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.OnClick;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/4 上午9:47
 */
public class QDGroupChatActivity extends QDChatActivity implements QDGroupCallBack, QDMessageCallBack {

    private List<QDGroupMember> memberList;
    private QDGroupMember selfMember;

    @BindString(R2.string.chat_record)
    String strRecord;
    @BindString(R2.string.group_loading_detail)
    String strLoading;
    @BindString(R2.string.group_get_error)
    String strGetError;
    @BindString(R2.string.group_forbidden_words)
    String strForbiddenWords;

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
                QDClient.getInstance().resendGroupMessage(message, callBack);
            } else {
                int fileStatus = message.getFileStatus();
                if (fileStatus != QDMessage.MSG_FILE_STATUS_UPLOAD_FAILED) {
                    QDClient.getInstance().resendGroupMessage(message, callBack);
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
                            QDClient.getInstance().sendGroupMessage(group, message, new QDResultCallBack() {
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
        isGroup = true;
        super.onCreate(savedInstanceState);
        spanList = new ArrayList<>();
        group = QDGroupHelper.getGroupById(chatId);
        if (group == null) {
            QDMessageHelper.setMessageReadWithGroupId(chatId);
            finish();
            return;
        }
        tvTitleName.setText(group.getName());
        tvTitleRight.setVisibility(View.VISIBLE);
        tvTitleRight.setBackgroundResource(R.drawable.ic_group_detail);

        memberList = QDGroupMemberHelper.getMembersByGroupId(group.getId());
        if (memberList.size() == 0) {
            getWarningDailog().setTip(strLoading);
            getWarningDailog().show();
            QDClient.getInstance().getGroupInfo(group.getId(), new QDResultCallBack<List<QDGroupMember>>() {
                @Override
                public void onError(String errorMsg) {
                    getWarningDailog().dismiss();
                    QDUtil.showToast(context, strGetError + errorMsg);
                }

                @Override
                public void onSuccess(List<QDGroupMember> groupMembers) {
                    getWarningDailog().dismiss();
                    memberList = groupMembers;
                    initMsgData();
                }
            });
        } else {
            initMsgData();
        }

        refreshAndLoad();
        setMsgRead();
        readDraft();
        QDGroupCallBackManager.getInstance().addCallBack(this);
        QDMessageCallBackManager.getInstance().addCallBack(this);
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
                List<QDMessage> messageList = QDMessageHelper.getMessageLimitWithGroupIdBeforeTime(chatId, time);
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
                List<QDMessage> messageList = QDMessageHelper.getMessageLimitWithGroupIdAfterTime(chatId, time + 1);
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

    private void initMsgData() {
        initBottomLayout();
        int selection;
        if (mMsgTime == 0) {
            msgList = QDMessageHelper.getMessageLimitWithGroupId(group.getId());
            selection = msgList.size();
        } else {
            msgList = new ArrayList<>();
            List<QDMessage> beforeList = QDMessageHelper.getMessageLimitWithGroupIdBeforeTime(chatId, mMsgTime, 4);
            List<QDMessage> afterList = QDMessageHelper.getMessageLimitWithGroupIdAfterTime(chatId, mMsgTime, 10);
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
        adapter.setMsgList(msgList);
        adapter.setListener(listener);
        adapter.setResendListener(resendListener);
        scrollListViewToSelection(selection);
    }

    private void initBottomLayout() {
        isBanned = false;
        etInput.setVisibility(View.VISIBLE);
        btnRecord.setVisibility(View.GONE);
        btnRecord.setText(strRecord);
        if (group.getFlag() == QDGroup.FLAG_BANNED) {
            selfMember = QDGroupMemberHelper.getMemberInfoByGroupIdAndUserId(group.getId(), QDLanderInfo.getInstance().getId());
            int role = selfMember.getRole();
            if (role == QDGroupMember.ROLE_NORMAL) {
                setBannedLayout();
            }
        }
    }

    private void setBannedLayout() {
        hideSmileAndFucLayout();
        QDUtil.closeKeybord(etInput, context);
        btnVoice.setBackgroundResource(R.drawable.btn_voice);
        btnSend.setVisibility(View.GONE);
        ivAdd.setVisibility(View.VISIBLE);
        etInput.setVisibility(View.GONE);
        btnRecord.setVisibility(View.VISIBLE);
        btnRecord.setText(strForbiddenWords);
        isBanned = true;
    }

    private void setMsgRead() {
        QDClient.getInstance().setGMsgRead(group.getId());
        QDMessageHelper.setMessageReadWithGroupId(group.getId());
    }

    @OnClick({R2.id.tv_title_right, R2.id.btn_chat_send})
    public void click(View view) {
        int i = view.getId();
        if (i == R.id.tv_title_right) {
            Intent intent = new Intent(context, QDGroupInfoActivity.class);
            intent.putExtra(QDIntentKeyUtil.INTENT_KEY_GROUP_ID, group.getId());
            startActivityForResult(intent, REQUEST_CLEAR_HISTORY);

        } else if (i == R.id.btn_chat_send) {
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

            QDAtClickSpan[] spans = etInput.getText().getSpans(0, etInput.length(), QDAtClickSpan.class);
            String content;
            if (spans.length == 0) {
                content = etInput.getText().toString().trim();
            } else {
                content = etInput.getText().toString();
            }
            if (TextUtils.isEmpty(content)) {
                return;
            }

            String content1 = QDChatSmiley.getInstance(context).strToSmileyInfo(content);

            if (isOpenBadWord && QDBadWordUtil.isContainBadWord(content1)) {
                if (alarmType == 1) {
                    QDClient.getInstance().sendBadWordAlarm(getBadData(content));
                }
                showBadTimeDialog();
                return;
            }

            QDResultCallBack<String> callBack = new QDResultCallBack<String>() {
                @Override
                public void onError(String msgId) {
                    updateMessageStatus(adapter.getMsgList(), msgId, QDMessage.MSG_STATUS_SEND_FAILED);
                }

                @Override
                public void onSuccess(String msgId) {
                    updateMessageStatus(adapter.getMsgList(), msgId, QDMessage.MSG_STATUS_SEND_OK);
                }
            };

            QDMessage message;
            if (spans.length == 0) {
                message = QDClient.getInstance().createGroupTextMessage(group, content);
                message.setSubject(QDChatSmiley.getInstance(context).strToSmileyInfo(message.getSubject()));
                QDClient.getInstance().resendGroupMessage(message, callBack);
//                    message = QDClient.getInstance().sendGroupMessage(group, content, callBack);
            } else {
                StringBuilder idString = new StringBuilder();
                StringBuilder sb = new StringBuilder();
                int lastIndex = 0;
                IMBTFManager manager = new IMBTFManager();
                for (QDAtClickSpan span : spans) {
                    int start = etInput.getText().getSpanStart(span);
                    int end = etInput.getText().getSpanEnd(span);
                    if (lastIndex < start) {
                        IMBTFText text = new IMBTFText(content.substring(lastIndex, start));
                        manager.addItem(text);
//                            sb.append(content.substring(lastIndex, start));
                    }
                    lastIndex = end + 1;
                    String atText = span.getUserId() + ";" + span.getUserName();
                    IMBTFAT imbtfat = new IMBTFAT(atText);
                    manager.addItem(imbtfat);
                    idString.append(span.getUserId()).append(",").append(span.getUserName()).append(";");
                }
                IMBTFText text = new IMBTFText(content.substring(lastIndex));
                manager.addItem(text);
//                    sb.append(content.substring(lastIndex));
                String atString = idString.substring(0, idString.length() - 1);
                message = QDClient.getInstance().createAtMessage(group, manager.toBTFXml(), content, atString, callBack);
                message.setSubject(QDChatSmiley.getInstance(context).strToSmileyInfo(message.getSubject()));
                QDClient.getInstance().resendGroupMessage(message, callBack);
            }
            adapter.addMsg(message);
            etInput.setText("");
            etInput.setFocusable(true);
            scrollListViewToBottom();

        }
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
        final QDMessage message = QDClient.getInstance().createGFileMessage(file, group.getId(), subject, type);
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
                QDClient.getInstance().sendGroupMessage(group, message, new QDResultCallBack() {
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
    protected void doSendCard(QDUser user) {
        final QDMessage message = QDClient.getInstance().createGCardMessage(user, group);
        adapter.addMsg(message);
        scrollListViewToBottom();
        QDClient.getInstance().sendGroupMessage(group, message, new QDResultCallBack() {
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

    @Override
    protected void doSendLocationMessage(String locInfo) {
        QDMessage message = QDClient.getInstance().sendGLocationMessage(group, locInfo, new QDResultCallBack<String>() {
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
    protected void onPause() {
        super.onPause();
        QDMessageHelper.setMessageReadWithGroupId(group.getId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QDGroupCallBackManager.getInstance().removeLast();
        QDMessageCallBackManager.getInstance().removeLast();
    }

    @Override
    public void groupChange(String groupId, QDGroup group, boolean isOwner) {
        if (group == null && groupId.equalsIgnoreCase(this.group.getId())) {
            finish();
        }
    }

    @Override
    public void groupMemberAdd(String groupId, List<QDGroupMember> groupMembers) {
        if (groupId.equalsIgnoreCase(group.getId())) {
            memberList.addAll(groupMembers);
        }
    }

    @Override
    public void groupMemberDelete(String groupId, List<String> memberIdList) {
        if (groupId.equalsIgnoreCase(group.getId())) {
            List<QDGroupMember> list = new ArrayList<>();
            for (QDGroupMember member : memberList) {
                if (!memberIdList.contains(member.getUid())) {
                    list.add(member);
                }
            }
            memberList = list;
        }
    }

    @Override
    public void groupInfoChange(String groupId, QDGroup group) {
        if (groupId.equalsIgnoreCase(this.group.getId())) {
            this.group = group;
            tvTitleName.setText(group.getName());
        }
    }

    @Override
    public void nickNameChange(String groupId, String userId, String nickName) {
        if (groupId.equalsIgnoreCase(group.getId())) {
            for (QDGroupMember member : memberList) {
                if (member.getUid().equalsIgnoreCase(userId)) {
                    member.setNickName(nickName);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void groupManagerChange(String groupId, String userId, boolean isManager) {
        if (groupId.equalsIgnoreCase(group.getId())) {
            for (QDGroupMember member : memberList) {
                if (member.getUid().equalsIgnoreCase(userId)) {
                    if (isManager) {
                        member.setRole(QDGroupMember.ROLE_MANAGER);
                    } else {
                        member.setRole(QDGroupMember.ROLE_NORMAL);
                    }
                    break;
                }
            }
            if (userId.equalsIgnoreCase(QDLanderInfo.getInstance().getId())) {
                if (isManager && isBanned) {
                    isBanned = false;
                    etInput.setVisibility(View.VISIBLE);
                    btnRecord.setVisibility(View.GONE);
                    btnRecord.setText(strRecord);
                }
                if (!isManager && isBanned) {
                    setBannedLayout();
                }
            }
        }
    }

    @Override
    public void groupOwnerChange(String groupId, String userId) {
        if (groupId.equalsIgnoreCase(group.getId())) {
            for (QDGroupMember member : memberList) {
                if (member.getRole() == QDGroupMember.ROLE_OWNER) {
                    member.setRole(QDGroupMember.ROLE_NORMAL);
                }
                if (member.getUid().equalsIgnoreCase(userId)) {
                    member.setRole(QDGroupMember.ROLE_OWNER);
                }
            }

            if (userId.equalsIgnoreCase(QDLanderInfo.getInstance().getId()) && isBanned) {
                isBanned = false;
                etInput.setVisibility(View.VISIBLE);
                btnRecord.setVisibility(View.GONE);
                btnRecord.setText(strRecord);
            }
        }
    }

    @Override
    public void groupCreateNotice(String groupId, String notice) {

    }

    @Override
    public void groupNotifyChange(String groupId, int notifyType) {

    }

    @Override
    public void groupBannedChange(String groupId, int flag) {
        if (groupId.equalsIgnoreCase(group.getId())) {
            group.setFlag(flag);
            initBottomLayout();
        }
    }

    @Override
    public void onReceive(List<QDMessage> message) {

    }

    @Override
    public void onReceiveGMsg(String groupId, List<QDMessage> messageList) {
        if (groupId.equalsIgnoreCase(group.getId())) {
            if (messageList.size() > 0) {
//                msgList.addAll(messageList);
//                adapter.setMsgList(msgList);
                adapter.addMsg(messageList);
                QDClient.getInstance().setGMsgRead(groupId);
                scrollListViewToBottom();
            }
        }
    }

    @Override
    public void onMsgReaded(String userId) {

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
}
