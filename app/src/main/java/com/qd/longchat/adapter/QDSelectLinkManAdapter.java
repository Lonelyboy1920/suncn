package com.qd.longchat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.dao.QDLinkman;
import com.qd.longchat.R;
import com.qd.longchat.holder.QDRootContactHolder;
import com.qd.longchat.util.QDBitmapUtil;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/3 下午3:44
 */
public class QDSelectLinkManAdapter extends BaseAdapter {

    private Context context;
    private List<QDLinkman> friendList;
    private int mode;
    private List<String> excludedUserIds;
    private List<String> selectUserIds;

    public QDSelectLinkManAdapter(Context context, List<QDLinkman> friendList) {
        this.context = context;
        this.friendList = friendList;
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

        QDLinkman friend = friendList.get(position);
        QDBitmapUtil.getInstance().createAvatar(context, friend.getId(), friend.getName(), friend.getIcon(), holder.itemIcon);
        holder.itemName.setText(friend.getName());
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
