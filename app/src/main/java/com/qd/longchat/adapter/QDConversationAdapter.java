package com.qd.longchat.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baoyz.swipemenulistview.BaseSwipeListAdapter;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuView;
import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.gavin.giframe.utils.GIUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.longchat.base.dao.QDConversation;
import com.longchat.base.dao.QDGroup;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDGroupHelper;
import com.longchat.base.databases.QDGroupNoticeHelper;
import com.longchat.base.databases.QDMessageHelper;
import com.longchat.base.databases.QDUserHelper;
import com.longchat.base.notify.QDNFAppInfo;
import com.longchat.base.util.QDGson;
import com.qd.longchat.R;
import com.qd.longchat.holder.QDConversationHolder;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDUtil;

import com.qd.longchat.widget.QDChatSmiley;

import java.util.HashMap;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/19 下午12:11
 */

public class QDConversationAdapter extends BaseSwipeListAdapter {

    private Context context;
    private List<QDConversation> conversationList;
    private Gson gson;

    public QDConversationAdapter(Context context) {
        this.context = context;
        gson = QDGson.getGson();
    }

    @Override
    public int getCount() {
        if (conversationList == null)
            return 0;
        return conversationList.size();
    }

    @Override
    public QDConversation getItem(int position) {
        return conversationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return conversationList.get(position).getIsTop();
    }

    @Override
    public void onUpdateSwipeMenu(int position, SwipeMenuView menuView) {
        int type = getItemViewType(position);
        if (type == QDConversation.TOP) {
            SwipeMenuItem item = menuView.getMenu().getMenuItem(0);
            String newTitle = context.getString(R.string.conversation_untop);
            if (!newTitle.equals(item.getTitle())) {
                item.setTitle(newTitle);
                item.setWidth(QDUtil.dp2px(context, 90));
                menuView.refreshView();
            }
        } else {
            SwipeMenuItem item = menuView.getMenu().getMenuItem(0);
            String newTitle = context.getString(R.string.conversation_top);
            if (!newTitle.equals(item.getTitle())) {
                item.setTitle(newTitle);
                item.setWidth(QDUtil.dp2px(context, 90));
                menuView.refreshView();
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDConversationHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_conversation, null);
            holder = new QDConversationHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDConversationHolder) convertView.getTag();
        }
        QDConversation conversation = conversationList.get(position);
        int type = conversation.getType();
        if (conversation.getIsTop() == QDConversation.TOP) {
            holder.ivItemTop.setVisibility(View.VISIBLE);
        } else {
            holder.ivItemTop.setVisibility(View.GONE);
        }

        holder.tvItemName.setCompoundDrawables(null, null, null, null);

        HashMap<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(QDUtil.readDraftFile())) {
            map = QDUtil.stringToMap(QDUtil.readDraftFile());
        }
        holder.llItemNotDisturbLayout.setVisibility(View.GONE);
        holder.tvItemPoint.setVisibility(View.GONE);
        switch (type) {
            case QDConversation.TYPE_PERSONAL:
                QDUser user = QDUserHelper.getUserById(conversation.getId());
                if (user != null) {
                    holder.tvItemName.setText(user.getName());
                    QDBitmapUtil.getInstance().createAvatar(context, conversation.getId(), user.getName(), user.getPic(), holder.ivItemIcon);
                } else {
                    holder.tvItemName.setText(conversation.getName());
                    QDBitmapUtil.getInstance().createAvatar(context, conversation.getId(), conversation.getName(), conversation.getIcon(), holder.ivItemIcon);
                }

                QDMessage message = QDMessageHelper.getLastMessageWithUserId(conversation.getId());
                if (!TextUtils.isEmpty(map.get(conversation.getId()))) {
                    String subject = map.get(conversation.getId());
                    String html = "<font color='#FF0000'>" + context.getString(R.string.text_draft) + "</font>"
                            + "<font>" + QDChatSmiley.getInstance(context).strToSmileyInfo(subject) + "</font>";
                    holder.tvItemSubname.setText(GIUtil.showHtmlInfo(html));
                } else {
                    if (message != null) {
                        if (message.getMsgType().equalsIgnoreCase(QDMessage.MSG_TYPE_CANCEL)) {
                            if (message.getDirection() == QDMessage.DIRECTION_OUT) {
                                String title = context.getResources().getString(R.string.str_you) + context.getResources().getString(R.string.revoke_msg);
                                holder.tvItemSubname.setText(title);
                            } else {
                                holder.tvItemSubname.setText(message.getSenderName() + context.getResources().getString(R.string.revoke_msg));
                            }
                        } else if (message.getMsgType().equalsIgnoreCase(QDMessage.MSG_TYPE_CARD)) {
                            JsonObject jsonObject = QDGson.getGson().fromJson(message.getContent(), JsonObject.class);
                            if (message.getDirection() == QDMessage.DIRECTION_OUT) {
                                holder.tvItemSubname.setText(context.getResources().getString(R.string.you_recommend) + jsonObject.get("title").getAsString());
                            } else {
                                holder.tvItemSubname.setText(context.getResources().getString(R.string.recommend_to_you) + jsonObject.get("title").getAsString());
                            }
                        } else {
                            holder.tvItemSubname.setText(QDChatSmiley.getInstance(context).strToSmileyInfo(message.getSubject()));
                        }
                        if (message.getMsgFlag() == QDMessage.MSGFLAG_SECRET) {
                            holder.tvItemSubname.setText(R.string.secret_message);
                        }
                    } else {
                        holder.tvItemSubname.setText("");
                    }
                }
                long time = conversation.getTime();
                holder.tvItemTime.setText(QDDateUtil.getConversationTime(time));
                int unreadCount = QDMessageHelper.getUnreadMessageCountWithUserId(conversation.getId());
                if (unreadCount != 0) {
                    setUnreadPoint(holder.tvItemPoint, unreadCount);
                } else {
                    holder.tvItemPoint.setVisibility(View.GONE);
                }
                break;
            case QDConversation.TYPE_GROUP:
                String groupId = conversation.getId();
                QDGroup group = QDGroupHelper.getGroupById(groupId);
                if (group != null) {
                    holder.tvItemName.setText(group.getName());
                    if (TextUtils.isEmpty(group.getIcon())) {
                        holder.ivItemIcon.setImageResource(R.mipmap.im_recent_group_icon);
                    } else {
                        QDBitmapUtil.getInstance().createAvatar(context, conversation.getId(), conversation.getName(), group.getIcon(), holder.ivItemIcon);
                    }
                } else {
                    holder.tvItemName.setText(conversation.getName());
                    holder.ivItemIcon.setImageResource(R.mipmap.im_recent_group_icon);
                }

                if (group != null) {
                    if (group.getType() == QDGroup.TYPE_ADMGROUP) {
                        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_group_gu);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        holder.tvItemName.setCompoundDrawables(null, null, drawable, null);
                    } else if (group.getType() == QDGroup.TYPE_DEPT) {
                        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_group_dept);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        holder.tvItemName.setCompoundDrawables(null, null, drawable, null);
                    }
                }

                QDMessage groupMessage = QDMessageHelper.getLastMessageWithGroupId(groupId);
                if (!TextUtils.isEmpty(map.get(conversation.getId()))) {
                    String subject = map.get(conversation.getId());
                    String html = "<font color='#FF0000'>" + context.getString(R.string.text_draft) + "</font>"
                            + "<font>" + QDChatSmiley.getInstance(context).strToSmileyInfo(subject) + "</font>";
                    holder.tvItemSubname.setText(GIUtil.showHtmlInfo(html));
                } else {
                    if (groupMessage != null) {
                        if (groupMessage.getMsgType().equalsIgnoreCase(QDMessage.MSG_TYPE_CANCEL)) {
                            if (groupMessage.getDirection() == QDMessage.DIRECTION_OUT) {
                                String title = context.getResources().getString(R.string.str_you) + context.getResources().getString(R.string.revoke_msg);
                                holder.tvItemSubname.setText(title);
                            } else {
                                holder.tvItemSubname.setText(groupMessage.getSenderName() + context.getResources().getString(R.string.revoke_msg));
                            }
                        } else {
                            holder.tvItemSubname.setText(QDChatSmiley.getInstance(context).strToSmileyInfo(groupMessage.getSubject()));
                        }
                    } else {
                        holder.tvItemSubname.setText(conversation.getSubname());
                    }
                }
                long time1 = conversation.getTime();
                holder.tvItemTime.setText(QDDateUtil.getConversationTime(time1));

                int unreadCount1 = QDMessageHelper.getUnreadMessageCountWithGroupId(conversation.getId());

                if (group != null) {
                    int notifyType = group.getNotifyType();
                    if (notifyType == QDGroup.NOTIFY_OFF) {
                        holder.llItemNotDisturbLayout.setVisibility(View.VISIBLE);
                        holder.tvItemPoint.setVisibility(View.GONE);
                        if (unreadCount1 == 0) {
                            holder.ivItemDisturbPoint.setVisibility(View.GONE);
                        } else {
                            holder.ivItemDisturbPoint.setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.llItemNotDisturbLayout.setVisibility(View.GONE);
                        holder.tvItemPoint.setVisibility(View.VISIBLE);
                        if (unreadCount1 != 0) {
                            setUnreadPoint(holder.tvItemPoint, unreadCount1);
                        } else {
                            holder.tvItemPoint.setVisibility(View.GONE);
                        }
                    }
                } else {
                    holder.llItemNotDisturbLayout.setVisibility(View.GONE);
                    holder.tvItemPoint.setVisibility(View.VISIBLE);
                    if (unreadCount1 != 0) {
                        setUnreadPoint(holder.tvItemPoint, unreadCount1);
                    } else {
                        holder.tvItemPoint.setVisibility(View.GONE);
                    }
                }
                break;
            case QDConversation.TYPE_APP:
                QDNFAppInfo info = gson.fromJson(conversation.getExtData(), QDNFAppInfo.class);

                if (info.getAppCode().equalsIgnoreCase("addon_notice")) {
                    holder.ivItemIcon.setImageResource(R.mipmap.im_recent_notice);
                } else {
                    String icon = QDUtil.getWebFileServer() + info.getIcon();

                    Glide.with(context).load(icon).apply(new RequestOptions().skipMemoryCache(true).placeholder(R.mipmap.ic_download_loading)
                            .error(R.mipmap.ic_download_failed)).into(holder.ivItemIcon);
                }


                if (info.getAppCode().equalsIgnoreCase("addon_acc")) {
                    holder.tvItemName.setText(info.getAccName());
                } else {
                    holder.tvItemName.setText(info.getAppName());
                }
                holder.tvItemSubname.setText(info.getTitle());
                holder.tvItemTime.setText(QDDateUtil.getConversationTime(conversation.getTime()));
                int unreadCount2 = QDMessageHelper.getUnreadMessageCountWithAppId(conversation.getId());
                if (unreadCount2 != 0) {
                    setUnreadPoint(holder.tvItemPoint, unreadCount2);
                } else {
                    holder.tvItemPoint.setVisibility(View.GONE);
                }
                break;
            case QDConversation.TYPE_SELF:
                QDMessage selfMessage = QDMessageHelper.getLastMessageWithSelfId(conversation.getId());
                holder.ivItemIcon.setImageResource(R.mipmap.im_recent_my_pc_icon);
                holder.tvItemName.setText(R.string.chat_my_pc);
                holder.tvItemTime.setText(QDDateUtil.getConversationTime(conversation.getTime()));
                int unreadCount3 = QDMessageHelper.getUnreadMessageCountWithMyPc(conversation.getId());
                if (unreadCount3 != 0) {
                    setUnreadPoint(holder.tvItemPoint, unreadCount3);
                } else {
                    holder.tvItemPoint.setVisibility(View.GONE);
                }
                holder.tvItemSubname.setText(QDChatSmiley.getInstance(context).strToSmileyInfo(selfMessage.getSubject()));
                break;
            case QDConversation.TYPE_GROUP_NOTICE:
                holder.ivItemIcon.setImageResource(R.drawable.ic_recent_group_notice);
                holder.tvItemName.setText(R.string.group_info_notice);
                holder.tvItemSubname.setText(conversation.getSubname());
                holder.tvItemTime.setText(QDDateUtil.getConversationTime(conversation.getTime()));
                int unreadCount4 = QDGroupNoticeHelper.getUnreadCount();
                if (unreadCount4 != 0) {
                    setUnreadPoint(holder.tvItemPoint, unreadCount4);
                } else {
                    holder.tvItemPoint.setVisibility(View.GONE);
                }
                break;
            case QDConversation.TYPE_MUSER:
                holder.ivItemIcon.setImageResource(R.mipmap.im_recent_multi_sent_icon);
                holder.tvItemName.setText(R.string.mass_message);
                holder.tvItemSubname.setText(conversation.getSubname());
                holder.tvItemTime.setText(QDDateUtil.getConversationTime(conversation.getTime()));
                int unreadCount5 = QDMessageHelper.getUnreadMassMessageCount();
                if (unreadCount5 != 0) {
                    setUnreadPoint(holder.tvItemPoint, unreadCount5);
                } else {
                    holder.tvItemPoint.setVisibility(View.GONE);
                }
                break;
        }
        return convertView;
    }

    private void setUnreadPoint(TextView textView, int count) {
        textView.setVisibility(View.VISIBLE);
        if (count > 99) {
            textView.setText("99+");
        } else {
            textView.setText(count + "");
        }
    }

    public void setConversationList(List<QDConversation> conversationList) {
        this.conversationList = conversationList;
        notifyDataSetChanged();
    }
}
