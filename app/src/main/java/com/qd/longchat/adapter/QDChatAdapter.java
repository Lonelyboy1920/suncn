package com.qd.longchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.gavin.giframe.utils.GIImageUtil;
import com.longchat.base.QDClient;
import com.longchat.base.btf.IMBTFAT;
import com.longchat.base.btf.IMBTFManager;
import com.longchat.base.btf.IMBTFText;
import com.longchat.base.client.QDIMClient;
import com.longchat.base.dao.QDGroupMember;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDGroupMemberHelper;
import com.longchat.base.databases.QDUserHelper;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDChatActivity;
import com.qd.longchat.config.QDChatContentLayout;
import com.qd.longchat.config.QDLanderInfo;
import com.qd.longchat.holder.QDChatHolder;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDDateUtil;
import com.suncn.ihold_zxztc.activity.message.Contact_DetailActivity;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/7 上午9:58
 */
public class QDChatAdapter extends BaseAdapter {

    private final static int TIME_INTERVAL = 60000; //消息时间显示的时间间隔
    private Context context;
    private List<QDMessage> msgList;
    private long lastTime;
    private Map<String, View> viewMap;
    private String chatId;
    private int mode;
    private List<String> selectedMsgId;

    private OnEditListener listener;

    private OnResendListener resendListener;

    private Map<String, View> confirmedMap;

    private List<String> msgIdList;

    public void setListener(OnEditListener listener) {
        this.listener = listener;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void addDelMessage(QDMessage message) {
        selectedMsgId.add(message.getMsgId());
        notifyDataSetChanged();
    }

    public void removeDelMessage(QDMessage message) {
        selectedMsgId.remove(message.getMsgId());
        notifyDataSetChanged();
    }

    public void clearSelectMessage() {
        selectedMsgId.clear();
    }

    public List<String> getSelectedMsgId() {
        return selectedMsgId;
    }

    public void setResendListener(OnResendListener resendListener) {
        this.resendListener = resendListener;
    }

    public interface OnEditListener {
        void onEdit(String text);
    }

    public interface OnResendListener {
        void onResend(QDMessage message);
    }

    public QDChatAdapter(Context context, String chatId) {
        this.context = context;
        this.chatId = chatId;
        viewMap = new HashMap<>();
        selectedMsgId = new ArrayList<>();
        confirmedMap = new HashMap<>();
        msgIdList = new ArrayList<>();
    }

    public List<QDMessage> getMsgList() {
        return msgList;
    }

    @Override
    public int getCount() {
        if (msgList == null) {
            return 0;
        }
        return msgList.size();
    }

    public int getIndexOfMsgId(String msdid) {
        return msgIdList.indexOf(msdid);
    }

    @Override
    public QDMessage getItem(int position) {
        return msgList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return msgList.get(position).getDirection();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDChatHolder holder;
        if (convertView == null) {
            if (getItemViewType(position) == QDMessage.DIRECTION_IN) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_left, null);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_right, null);
            }
            holder = QDChatHolder.init(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDChatHolder) convertView.getTag();
        }
        QDMessage message = msgList.get(position);
        bindMessage(holder, message, position);
        return convertView;
    }

    private void bindMessage(final QDChatHolder holder, final QDMessage message, int position) {
        String type = message.getMsgType();
        if (type.equalsIgnoreCase(QDMessage.MSG_TYPE_NOTIFY) || type.equalsIgnoreCase(QDMessage.MSG_TYPE_CANCEL)) {
            holder.itemCheck.setVisibility(View.GONE);
            holder.itemContentLayout.setVisibility(View.GONE);
            holder.itemChatIcon.setVisibility(View.GONE);
            holder.itemMsgTime.setVisibility(View.VISIBLE);
            if (type.equalsIgnoreCase(QDMessage.MSG_TYPE_NOTIFY)) {
                holder.itemMsgTime.setText(message.getSubject());
            } else {
                if (message.getDirection() == QDMessage.DIRECTION_OUT) {
                    String title = context.getResources().getString(R.string.str_you) + context.getResources().getString(R.string.revoke_msg);
                    holder.itemMsgTime.setText(title);
                    IMBTFManager manager = new IMBTFManager(message.getContent());
                    long currentTime = System.currentTimeMillis();
                    long createDate = message.getCreateDate() / 1000;
                    if (currentTime + QDIMClient.getInstance().getLoginInfo().getSTimeOffset() - createDate < 5 * 60 * 1000 && manager.getItemList().size() != 0 && (manager.getItemList().get(0) instanceof IMBTFText || manager.getItemList().get(0) instanceof IMBTFAT)) {
                        holder.itemMsgTime.setMovementMethod(LinkMovementMethod.getInstance());
                        SpannableString clickString = new SpannableString(context.getResources().getString(R.string.re_edit));
                        clickString.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                if (listener != null) {
                                    listener.onEdit(message.getContent());
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(false);
                                ds.setColor(context.getResources().getColor(R.color.colorBtnBlue));
                            }
                        }, 0, clickString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        holder.itemMsgTime.append(clickString);
                    }
                } else {
                    holder.itemMsgTime.setText(message.getSenderName() + context.getResources().getString(R.string.revoke_msg));
                }
            }
        } else {
            holder.itemContentLayout.setVisibility(View.VISIBLE);
            holder.itemChatIcon.setVisibility(View.VISIBLE);
            if (mode == QDChatActivity.MODE_DEL) {
                holder.itemCheck.setVisibility(View.VISIBLE);
                if (selectedMsgId.contains(message.getMsgId())) {
                    holder.itemCheck.setImageResource(R.drawable.ic_round_selected);
                } else {
                    holder.itemCheck.setImageResource(R.drawable.ic_round_unselected);
                }
            } else {
                holder.itemCheck.setVisibility(View.GONE);
            }

            long offsetTime = QDClient.getInstance().getLoginInfo().getSTimeOffset(); //服务器和本地的时间差
            long msgTime = (message.getCreateDate() / 1000) - offsetTime; //微秒变为毫秒
            if (position == 0) {
                holder.itemMsgTime.setVisibility(View.VISIBLE);
                holder.itemMsgTime.setText(QDDateUtil.getMsgTime(msgTime));
            } else {
                QDMessage lastMsg = msgList.get(position - 1);
                lastTime = lastMsg.getCreateDate() / 1000 - offsetTime;
                if (msgTime - lastTime > TIME_INTERVAL) {
                    holder.itemMsgTime.setVisibility(View.VISIBLE);
                    holder.itemMsgTime.setText(QDDateUtil.getMsgTime(msgTime));
                } else {
                    holder.itemMsgTime.setVisibility(View.GONE);
                }
            }

            int direction = message.getDirection();
            if (direction == QDMessage.DIRECTION_OUT) {
                GIImageUtil.loadImg(context, holder.itemChatIcon, Utils.formatFileUrl(context, QDLanderInfo.getInstance().getPic()), 1);
                holder.itemChatIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userId =QDLanderInfo.getInstance().getId();
                        Intent intent = new Intent(context, Contact_DetailActivity.class);
                        intent.putExtra("strQdUserId", userId);
                        context.startActivity(intent);
                    }
                });
            } else {
                String userId = message.getSenderId();
                QDUser user = QDUserHelper.getUserById(userId);
                GIImageUtil.loadImg(context, holder.itemChatIcon, Utils.formatFileUrl(context, user.getPic()), 1);
                if (!TextUtils.isEmpty(message.getGroupId())) {
                    holder.itemChatName.setVisibility(View.VISIBLE);
                    QDGroupMember member = QDGroupMemberHelper.getMemberInfoByGroupIdAndUserId(message.getGroupId(), message.getSenderId());
                    if (member == null) {
                        holder.itemChatName.setText(message.getSenderName());
                    } else {
                        holder.itemChatName.setText(member.getNickName());
                    }
                } else {
                    holder.itemChatName.setVisibility(View.GONE);
                }

                holder.itemChatIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userId = message.getSenderId();
                        Intent intent = new Intent(context, Contact_DetailActivity.class);
                        intent.putExtra("strQdUserId", userId);
                        context.startActivity(intent);
                    }
                });
            }

            if (message.getMsgType().equalsIgnoreCase(QDMessage.MSG_TYPE_CONFIRMED)) {
                if (confirmedMap.get(message.getMsgId()) != null) {
                    bindViews(holder.itemContainer, confirmedMap.get(message.getMsgId()));
                } else {
                    View view = getContentView(type, message);
                    if (view == null) {
                        return;
                    }
                    confirmedMap.put(message.getMsgId(), view);
                    bindViews(holder.itemContainer, view);
                }
            } else {
                if (viewMap.get(message.getMsgId()) != null) {
                    bindViews(holder.itemContainer, viewMap.get(message.getMsgId()));
                } else {
                    View view = getContentView(type, message);
                    if (view == null) {
                        return;
                    }
                    viewMap.put(message.getMsgId(), view);
                    bindViews(holder.itemContainer, view);
                }
            }

            if (message.getMsgFlag() == QDMessage.MSGFLAG_SECRET) {
                holder.itemBurn.setVisibility(View.VISIBLE);
            } else {
                holder.itemBurn.setVisibility(View.GONE);
            }
            if (direction == QDMessage.DIRECTION_OUT) {
                if (!TextUtils.isEmpty(message.getGroupId()) || message.getSenderId().equalsIgnoreCase(message.getReceiverId())) {
                    updateStatus(holder, message, true);
                } else {
                    updateStatus(holder, message, false);
                }
            }
            int fileStatus = message.getFileStatus();
            if (fileStatus == QDMessage.MSG_FILE_STATUS_UPLOAD_FAILED) {
                holder.itemStatus.setVisibility(View.GONE);
                holder.itemProgress.setVisibility(View.GONE);
                holder.itemFailed.setVisibility(View.VISIBLE);
                holder.itemFailed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resendListener.onResend(message);
                    }
                });
            }

            holder.itemCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedMsgId.contains(message.getMsgId())) {
                        holder.itemCheck.setImageResource(R.drawable.ic_round_unselected);
                        removeDelMessage(message);
                    } else {
                        holder.itemCheck.setImageResource(R.drawable.ic_round_selected);
                        addDelMessage(message);
                    }
                }
            });
        }
    }

    private View getContentView(String type, QDMessage message) {
        switch (type) {
            case QDMessage.MSG_TYPE_TEXT:
                return QDChatContentLayout.createTextView(context, message);
            case QDMessage.MSG_TYPE_IMAGE:
                return QDChatContentLayout.createImageView(context, message, chatId);
            case QDMessage.MSG_TYPE_FILE:
                return QDChatContentLayout.createFileView(context, message);
            case QDMessage.MSG_TYPE_VOICE:
                return QDChatContentLayout.createVoiceView(context, message);
            case QDMessage.MSG_TYPE_SHOOT:
                return QDChatContentLayout.createShootView(context, message);
            case QDMessage.MSG_TYPE_LOCATION:
                return QDChatContentLayout.createLocationView(context, message);
            case QDMessage.MSG_TYPE_CMD:
                return QDChatContentLayout.createCmdView(context, message);
            case QDMessage.MSG_TYPE_AUDIO:
            case QDMessage.MSG_TYPE_VIDEO:
                return QDChatContentLayout.createVideoView(context, message);
            case QDMessage.MSG_TYPE_CONFIRM:
                return QDChatContentLayout.createSignView(context, message);
            case QDMessage.MSG_TYPE_CONFIRMED:
                return QDChatContentLayout.createConfirmedView(context, message);
            case QDMessage.MSG_TYPE_LINK:
                return QDChatContentLayout.createLinkView(context, message);
            case QDMessage.MSG_TYPE_CARD:
                return QDChatContentLayout.createCardView(context, message);
        }
        return null;
    }

    private void updateStatus(QDChatHolder holder, final QDMessage message, boolean isGroup) {
        switch (message.getStatus()) {
            case QDMessage.MSG_STATUS_SENDING:
                long offset = (System.currentTimeMillis() + QDClient.getInstance().getLoginInfo().getSTimeOffset()) - message.getCreateDate() / 1000;
                if (offset > 15 * 1000) {
                    holder.itemProgress.setVisibility(View.GONE);
                    holder.itemFailed.setVisibility(View.VISIBLE);
                    holder.itemFailed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            resendListener.onResend(message);
                        }
                    });
                } else {
                    holder.itemProgress.setVisibility(View.VISIBLE);
                    holder.itemFailed.setVisibility(View.GONE);
                }
                holder.itemStatus.setVisibility(View.GONE);
                break;
            case QDMessage.MSG_STATUS_SEND_OK:
                if (isGroup) {
                    holder.itemStatus.setVisibility(View.GONE);
                } else {
                    holder.itemStatus.setVisibility(View.VISIBLE);
                    holder.itemStatus.setTextColor(context.getResources().getColor(R.color.colorBtnBlue));
                    holder.itemStatus.setText(R.string.str_unread);
                }
                holder.itemProgress.setVisibility(View.GONE);
                holder.itemFailed.setVisibility(View.GONE);
                break;
            case QDMessage.MSG_STATUS_SEND_FAILED:
                holder.itemStatus.setVisibility(View.GONE);
                holder.itemProgress.setVisibility(View.GONE);
                holder.itemFailed.setVisibility(View.VISIBLE);
                holder.itemFailed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resendListener.onResend(message);
                    }
                });
                break;
            case QDMessage.MSG_STATUS_READ_ACK:
                holder.itemStatus.setVisibility(View.GONE);
                holder.itemProgress.setVisibility(View.GONE);
                holder.itemFailed.setVisibility(View.GONE);
                break;
            case QDMessage.MSG_STATUS_RECEIVE:
                if (message.getDirection() == QDMessage.DIRECTION_OUT) {
                    holder.itemStatus.setVisibility(View.GONE);
                    holder.itemProgress.setVisibility(View.GONE);
                    holder.itemFailed.setVisibility(View.GONE);
                }
                break;

        }
    }

    private void bindViews(LinearLayout container, View view) {
        container.removeAllViews();
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null)
            viewGroup.removeAllViews();
        container.addView(view);
    }

    public void setMsgList(List<QDMessage> msgList) {
        if (this.msgList == null) {
            this.msgList = new ArrayList<>();
        } else {
            this.msgList.clear();
        }
        msgIdList.clear();
        for (QDMessage message : msgList) {
            if (!msgIdList.contains(message.getMsgId())) {
                msgIdList.add(message.getMsgId());
                this.msgList.add(message);
            }
        }
        notifyDataSetChanged();
    }

    public void addMsg(QDMessage msg) {
        this.msgList.add(msg);
        this.msgIdList.add(msg.getMsgId());
        notifyDataSetChanged();
    }

    public void addMsg(List<QDMessage> msgList) {
        for (QDMessage message : msgList) {
            if (!msgIdList.contains(message.getMsgId())) {
                msgIdList.add(message.getMsgId());
                this.msgList.add(message);
            }
        }
        notifyDataSetChanged();
    }


    public void removeMsg(String msgId) {
        if (msgIdList.contains(msgId)) {
            int index = msgIdList.indexOf(msgId);
            msgIdList.remove(index);
            msgList.remove(index);
            notifyDataSetChanged();
        }
    }

}
