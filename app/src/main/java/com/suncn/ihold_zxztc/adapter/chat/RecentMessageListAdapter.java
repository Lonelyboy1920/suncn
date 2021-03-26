package com.suncn.ihold_zxztc.adapter.chat;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gavin.giframe.utils.GIImageUtil;
import com.gavin.giframe.utils.GIStringUtil;
import com.gavin.giframe.widget.GITextView;
import com.gavin.giframe.widget.RoundImageView;
import com.longchat.base.dao.QDConversation;
import com.longchat.base.dao.QDGroup;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.databases.QDGroupHelper;
import com.longchat.base.databases.QDMessageHelper;
import com.qd.longchat.widget.QDChatSmiley;
import com.suncn.ihold_zxztc.R;
import com.suncn.ihold_zxztc.bean.chat.UserInfoBean;
import com.suncn.ihold_zxztc.utils.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * 最近聊天记录适配器
 */
public class RecentMessageListAdapter extends BaseQuickAdapter<UserInfoBean, RecentMessageListAdapter.ViewHolder> {
    private Activity activity;

    public RecentMessageListAdapter(Activity activity) {
        super(R.layout.item_rv_message_main);
        this.activity = activity;
    }


    @Override
    protected void convert(@NotNull ViewHolder viewHolder, UserInfoBean data) {
        viewHolder.name_TextView.setText(data.getStrLinkName());
        viewHolder.time_TextView.setText(data.getStrSendDate());
        if (data.getIsTop() == QDConversation.TOP) {
            viewHolder.iv_top.setVisibility(View.VISIBLE);
        } else {
            viewHolder.iv_top.setVisibility(View.GONE);
        }
        if (Integer.parseInt(data.getStrMsgType()) == QDConversation.TYPE_PERSONAL) {
            GIImageUtil.loadImg(activity, viewHolder.header_ImageView, Utils.formatFileUrl(activity, data.getStrPathUrl()), 1);
            QDMessage message = QDMessageHelper.getLastMessageWithUserId(data.getStrLinkId());
            if (message != null && message.getMsgType().equalsIgnoreCase(QDMessage.MSG_TYPE_CANCEL)) {
                if (message.getDirection() == QDMessage.DIRECTION_OUT) {
                    String title = activity.getResources().getString(com.qd.longchat.R.string.str_you) + activity.getResources().getString(com.qd.longchat.R.string.revoke_msg);
                    viewHolder.message_TextView.setText(title);
                } else {
                    viewHolder.message_TextView.setText(message.getSenderName() + activity.getResources().getString(com.qd.longchat.R.string.revoke_msg));
                }
            } else {
                viewHolder.message_TextView.setText(QDChatSmiley.getInstance(activity).strToSmileyInfo(data.getStrMsgContent()));
            }
            if (0 != data.getIntReadStateNo() && data.getIntReadStateNo() <= 99) {
                viewHolder.unreadCount_TextView.setVisibility(View.VISIBLE);
                viewHolder.unreadCount_TextView.setText(data.getIntReadStateNo() + "");
            } else if (data.getIntReadStateNo() > 99) {
                viewHolder.unreadCount_TextView.setVisibility(View.VISIBLE);
                viewHolder.unreadCount_TextView.setText("99+");
            } else {
                viewHolder.unreadCount_TextView.setVisibility(View.INVISIBLE);
            }
            viewHolder.iv_notice.setVisibility(View.GONE);
            viewHolder.tv_unread.setVisibility(View.GONE);
        } else {
            QDMessage groupMessage = QDMessageHelper.getLastMessageWithGroupId(data.getStrLinkId());
            QDGroup group = QDGroupHelper.getGroupById(data.getStrLinkId());
            if (GIStringUtil.isNotBlank(data.getStrMsgType()) && Integer.parseInt(data.getStrMsgType()) == QDConversation.TYPE_GROUP_NOTICE) {
                viewHolder.name_TextView.setText("群通知");
                GIImageUtil.loadImg(activity, viewHolder.header_ImageView, "", activity.getResources().getDrawable(R.mipmap.group_notice_icon));
            } else {
                GIImageUtil.loadImg(activity, viewHolder.header_ImageView, "", activity.getResources().getDrawable(R.mipmap.im_recent_group_icon));
            }
            if (group != null && group.getNotifyType() == QDGroup.NOTIFY_OFF) {
                viewHolder.iv_notice.setVisibility(View.VISIBLE);
                if (0 != data.getIntReadStateNo()) {
                    viewHolder.tv_unread.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.tv_unread.setVisibility(View.GONE);
                }
                viewHolder.unreadCount_TextView.setVisibility(View.GONE);
            } else {
                viewHolder.iv_notice.setVisibility(View.GONE);
                viewHolder.tv_unread.setVisibility(View.GONE);
                if (0 != data.getIntReadStateNo() && data.getIntReadStateNo() <= 99) {
                    viewHolder.unreadCount_TextView.setVisibility(View.VISIBLE);
                    viewHolder.unreadCount_TextView.setText(data.getIntReadStateNo() + "");
                } else if (data.getIntReadStateNo() > 99) {
                    viewHolder.unreadCount_TextView.setVisibility(View.VISIBLE);
                    viewHolder.unreadCount_TextView.setText("99+");
                } else {
                    viewHolder.unreadCount_TextView.setVisibility(View.INVISIBLE);
                }
            }
            if (groupMessage != null) {
                if (groupMessage.getMsgType().equalsIgnoreCase(QDMessage.MSG_TYPE_CANCEL)) {
                    if (groupMessage.getDirection() == QDMessage.DIRECTION_OUT) {
                        String title = activity.getResources().getString(com.qd.longchat.R.string.str_you) + activity.getResources().getString(com.qd.longchat.R.string.revoke_msg);
                        viewHolder.message_TextView.setText(title);
                    } else {
                        viewHolder.message_TextView.setText(groupMessage.getSenderName() + activity.getResources().getString(com.qd.longchat.R.string.revoke_msg));
                    }
                } else {
                    viewHolder.message_TextView.setText(QDChatSmiley.getInstance(activity).strToSmileyInfo(data.getStrMsgContent()));
                }
            } else {
                viewHolder.message_TextView.setText(QDChatSmiley.getInstance(activity).strToSmileyInfo(data.getStrMsgContent()));
            }
        }
    }

    public class ViewHolder extends BaseViewHolder {
        private RoundImageView header_ImageView;
        private TextView name_TextView;
        private GITextView time_TextView;
        private TextView message_TextView;
        private TextView unreadCount_TextView;
        private ImageView iv_top;
        private ImageView iv_notice;
        private TextView tv_unread;

        public ViewHolder(View itemView) {
            super(itemView);
            header_ImageView = itemView.findViewById(R.id.iv_header);
            name_TextView = itemView.findViewById(R.id.tv_name);
            time_TextView = itemView.findViewById(R.id.tv_time);
            message_TextView = itemView.findViewById(R.id.tv_message);
            unreadCount_TextView = itemView.findViewById(R.id.tv_unread_count);
            iv_top = itemView.findViewById(R.id.iv_top);
            iv_notice = itemView.findViewById(R.id.iv_notice);
            tv_unread = itemView.findViewById(R.id.tv_unread);
        }

    }
}
