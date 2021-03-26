package com.qd.longchat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.dao.QDConversation;
import com.longchat.base.dao.QDGroup;
import com.longchat.base.dao.QDUser;
import com.longchat.base.databases.QDGroupHelper;
import com.longchat.base.databases.QDUserHelper;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDContactActivity;
import com.qd.longchat.holder.QDRootContactHolder;
import com.qd.longchat.util.QDBitmapUtil;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/3 下午3:44
 */
public class QDSelectContactAdapter extends BaseAdapter {

    private Context context;
    private List<QDConversation> friendList;
    private int mode;
    private List<String> excludedUserIds;
    private List<String> selectUserIds;

    public QDSelectContactAdapter(Context context, List<QDConversation> friendList, int mode) {
        this.context = context;
        this.friendList = friendList;
        this.mode = mode;
    }

    @Override
    public int getCount() {
        return friendList.size();
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
        QDRootContactHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_contact, null);
            holder = QDRootContactHolder.init(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDRootContactHolder) convertView.getTag();
        }

        QDConversation conversation = friendList.get(position);

        if (mode == QDContactActivity.MODE_MULTI) {
            if (excludedUserIds.contains(conversation.getId())) {
                holder.itemCheck.setVisibility(View.GONE);
            } else {
                holder.itemCheck.setVisibility(View.VISIBLE);
                if (selectUserIds.contains(conversation.getId())) {
                    holder.itemCheck.setImageResource(R.drawable.ic_round_selected);
                } else {
                    holder.itemCheck.setImageResource(R.drawable.ic_round_unselected);
                }
            }
        } else {
            holder.itemCheck.setVisibility(View.GONE);
        }
        if (conversation.getType() == QDConversation.TYPE_PERSONAL) {
            QDUser user = QDUserHelper.getUserById(conversation.getId());
            QDBitmapUtil.getInstance().createAvatar(context, conversation.getId(), conversation.getName(), user.getPic(), holder.itemIcon);
        } else {
            QDGroup group = QDGroupHelper.getGroupById(conversation.getId());
            if (TextUtils.isEmpty(group.getIcon())) {
                holder.itemIcon.setImageResource(R.mipmap.im_recent_group_icon);
            } else {
                QDBitmapUtil.getInstance().createAvatar(context, conversation.getId(), conversation.getName(), group.getIcon(), holder.itemIcon);
            }
        }

        holder.itemName.setText(conversation.getName());
        holder.itemFunc.setVisibility(View.GONE);
        holder.itemSubname.setVisibility(View.GONE);
        convertView.setBackgroundColor(Color.WHITE);
        return convertView;
    }

    public void setExcludedUserIds(List<String> excludedUserIds) {
        this.excludedUserIds = excludedUserIds;
    }

    public void setSelectUserIds(List<String> selectUserIds) {
        this.selectUserIds = selectUserIds;
    }
}
