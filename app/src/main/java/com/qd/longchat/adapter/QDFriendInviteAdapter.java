package com.qd.longchat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.QDClient;
import com.longchat.base.callback.QDResultCallBack;
import com.longchat.base.dao.QDFriendInvite;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDUserHelper;
import com.qd.longchat.R;
import com.qd.longchat.holder.QDFriendInviteHolder;
import com.qd.longchat.util.QDBitmapUtil;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/12 下午4:16
 */
public class QDFriendInviteAdapter extends BaseAdapter {

    private Context context;
    private List<QDFriendInvite> inviteList;
    private QDResultCallBack callBack;

    public QDFriendInviteAdapter(Context context, List<QDFriendInvite> inviteList, QDResultCallBack callBack) {
        this.context = context;
        this.inviteList = inviteList;
        this.callBack = callBack;
    }

    public void setInviteList(List<QDFriendInvite> inviteList) {
        this.inviteList = inviteList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return inviteList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDFriendInviteHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend_invite, null);
            holder = new QDFriendInviteHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDFriendInviteHolder) convertView.getTag();
        }
        final QDFriendInvite invite = inviteList.get(position);
        QDBitmapUtil.getInstance().createAvatar(context, invite.getUserId(), invite.getUserName(), holder.itemPhoto);
        holder.itemName.setText(invite.getUserName());
        holder.itemInfo.setText(context.getResources().getString(R.string.friend_invite_tag) + " " + invite.getDesc());
        int reply = invite.getReply();
        if (reply == QDFriendInvite.REPLY_NORMAL) {
            holder.itemBtn.setBackgroundResource(R.drawable.ic_rounded_rectangle_btn);
            holder.itemBtn.setTextColor(Color.WHITE);
            holder.itemBtn.setText(context.getResources().getString(R.string.friend_invite_agree));
            holder.itemBtn.setEnabled(true);

            holder.itemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QDUser user = QDUserHelper.getUserById(invite.getUserId());
                    if (user == null) {
                        QDClient.getInstance().fetchUserInfo(invite.getUserId());
                        user = new QDUser();
                        user.setId(invite.getUserId());
                        user.setName(invite.getUserName());
                    }
                    QDClient.getInstance().replyInvite(user, QDFriendInvite.REPLY_AGREE, callBack);
                }
            });
        } else if (reply == QDFriendInvite.REPLY_AGREE) {
            holder.itemBtn.setBackground(null);
            holder.itemBtn.setTextColor(context.getResources().getColor(R.color.colorTextUnFocused));
            holder.itemBtn.setText(context.getResources().getString(R.string.friend_invite_had_allow));
            holder.itemBtn.setEnabled(false);
        } else {
            holder.itemBtn.setBackground(null);
            holder.itemBtn.setTextColor(context.getResources().getColor(R.color.colorTextUnFocused));
            holder.itemBtn.setText(context.getResources().getString(R.string.friend_invite_had_refuse));
            holder.itemBtn.setEnabled(false);
        }
        return convertView;
    }
}
