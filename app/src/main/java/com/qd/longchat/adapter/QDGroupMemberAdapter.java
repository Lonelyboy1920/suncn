package com.qd.longchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gavin.giframe.utils.GIImageUtil;
import com.qd.longchat.R;
import com.longchat.base.dao.QDGroupMember;
import com.qd.longchat.util.QDBitmapUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/2 下午4:52
 */
public class QDGroupMemberAdapter extends BaseAdapter {

    private Context context;
    private List<QDGroupMember> memberList;
    private boolean isVisible;

    public QDGroupMemberAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        if (memberList == null)
            return 0;
        if (isVisible) {
            return memberList.size() + 2;
        }
        return memberList.size();
    }

    @Override
    public QDGroupMember getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_group_member, null);
        ImageView itemIcon = (ImageView) convertView.findViewById(R.id.iv_item_icon);
        TextView itemName = (TextView) convertView.findViewById(R.id.tv_item_name);
        if (position == memberList.size()) {
            itemIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.im_group_member_add));
            itemName.setText("");
        } else if (position == memberList.size() + 1) {
            itemIcon.setImageDrawable(context.getResources().getDrawable(R.mipmap.im_group_member_delete));
            itemName.setText("");
        } else {
            QDGroupMember member = memberList.get(position);
            itemName.setText(member.getNickName());
            GIImageUtil.loadImg(context,itemIcon, Utils.formatFileUrl(context,member.getIcon()),1);
        }
        return convertView;
    }

    public void setMemberList(List<QDGroupMember> memberList) {
        this.memberList = memberList;
        notifyDataSetChanged();
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
}
