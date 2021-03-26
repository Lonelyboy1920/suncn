package com.qd.longchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.qd.longchat.R;
import com.qd.longchat.util.QDResourceUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/19 下午2:34
 */
public class QDGroupIconAdapter extends BaseAdapter {

    private Context context;
    private int index;

    public QDGroupIconAdapter(Context context) {
        this.context = context;
        index = 0;
    }

    public void setIndex(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    class Holder {
        public CircleImageView ivIcon;
    }

    @Override
    public int getCount() {
        return 8;
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
        Holder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group_icon, null);
            holder = new Holder();
            holder.ivIcon = convertView.findViewById(R.id.iv_item_icon);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        if (position == 0) {
            holder.ivIcon.setImageResource(R.mipmap.im_take_photo_icon);
        } else {
            holder.ivIcon.setImageResource(QDResourceUtil.getMipmapId(context, "ic_group_" + position));
        }

        if (index != 0 && index == position) {
            holder.ivIcon.setBorderColor(context.getResources().getColor(R.color.colorSelectedBound));
        } else {
            holder.ivIcon.setBorderColor(context.getResources().getColor(R.color.colorUnSelectedBound));
        }
        return convertView;
    }
}
