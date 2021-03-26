package com.qd.longchat.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gavin.giframe.utils.GIImageUtil;
import com.longchat.base.dao.QDGroup;
import com.qd.longchat.R;
import com.qd.longchat.holder.QDRootContactHolder;
import com.suncn.ihold_zxztc.utils.Utils;


import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/30 上午11:49
 */

public class QDGroupAdapter extends BaseAdapter {

    private Context context;
    private List<QDGroup> groups;

    public QDGroupAdapter(Context context, List<QDGroup> groups) {
        this.context = context;
        this.groups = groups;
    }

    @Override
    public int getCount() {
        return groups.size();
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
        QDGroup group = groups.get(position);
        String icon = group.getIcon();
        GIImageUtil.loadImg(context,holder.itemIcon, Utils.formatFileUrl(context,icon),context.getResources().getDrawable(R.mipmap.im_recent_group_icon));
        holder.itemIcon.setImageResource(R.mipmap.im_recent_group_icon);
        holder.itemName.setText(group.getName());
        if (group.getNotifyType() == QDGroup.NOTIFY_OFF) {
            holder.itemRightName.setVisibility(View.VISIBLE);
            holder.itemRightName.setBackground(context.getResources().getDrawable(R.mipmap.no_notice_icon));
        } else {
            holder.itemRightName.setVisibility(View.GONE);
        }
        if (group.getType() == QDGroup.TYPE_ADMGROUP) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_group_gu);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.itemName.setCompoundDrawables(null, null, drawable, null);
        } else if (group.getType() == QDGroup.TYPE_DEPT) {
            Drawable drawable = context.getResources().getDrawable(R.drawable.ic_group_dept);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.itemName.setCompoundDrawables(null, null, drawable, null);
        } else {
            holder.itemName.setCompoundDrawables(null, null, null, null);
        }

        String desc = group.getDesc();
        holder.itemSubname.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(desc)) {
            holder.itemSubname.setText(R.string.group_info_desc_empty);
        } else {
            holder.itemSubname.setText(desc);
        }
        return convertView;
    }
}
