package com.qd.longchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gavin.giframe.utils.GIImageUtil;
import com.qd.longchat.R;
import com.qd.longchat.activity.QDGroupMemberActivity;
import com.longchat.base.dao.QDGroupMember;
import com.qd.longchat.holder.QDRootContactHolder;
import com.qd.longchat.util.QDBitmapUtil;
import com.suncn.ihold_zxztc.utils.Utils;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/6/5 下午1:54
 */
public class QDMemberAdapter extends BaseAdapter {

    private Context context;
    private List<QDGroupMember> memberList;
    private int mode;
    private boolean isOwner;
    private List<String> selectedIdList;

    public QDMemberAdapter(Context context, List<QDGroupMember> memberList, int mode, boolean isOwner) {
        this.context = context;
        this.memberList = memberList;
        this.mode = mode;
        this.isOwner = isOwner;
    }

    @Override
    public int getCount() {
        return memberList.size();
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
        QDGroupMember member = memberList.get(position);
        GIImageUtil.loadImg(context,holder.itemIcon, Utils.formatFileUrl(context,member.getIcon()),1);
        holder.itemName.setText(member.getNickName());
        int role = member.getRole();
        if (role == QDGroupMember.ROLE_OWNER) {
            holder.itemRightName.setText(context.getResources().getString(R.string.group_member_owner));
        } else if (role == QDGroupMember.ROLE_MANAGER) {
            holder.itemRightName.setText(context.getResources().getString(R.string.group_member_manager));
        } else {
            holder.itemRightName.setText("");
        }
        if (mode == QDGroupMemberActivity.MODE_DEL_MEMBER) {
            if (role == QDGroupMember.ROLE_OWNER) {
                holder.itemCheck.setVisibility(View.GONE);
            } else if (role == QDGroupMember.ROLE_MANAGER) {
                if (isOwner) {
                    holder.itemRightName.setText("");
                    holder.itemCheck.setVisibility(View.VISIBLE);
                } else {
                    holder.itemCheck.setVisibility(View.GONE);
                }
            } else {
                holder.itemCheck.setVisibility(View.VISIBLE);
            }
            if (selectedIdList != null) {
                if (selectedIdList.contains(member.getUid())) {
                    holder.itemCheck.setImageResource(R.drawable.ic_round_selected);
                } else {
                    holder.itemCheck.setImageResource(R.drawable.ic_round_unselected);
                }
            }
        } else {
            holder.itemCheck.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void setSelectedIdList(List<String> selectedIdList) {
        this.selectedIdList = selectedIdList;
        notifyDataSetChanged();
    }
}
