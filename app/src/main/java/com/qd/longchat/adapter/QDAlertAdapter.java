package com.qd.longchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;

import java.util.List;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/11 上午11:24
 */

public class QDAlertAdapter extends BaseAdapter {

    private Context context;
    private List<String> strList;
    private boolean isVisible;

    public QDAlertAdapter(Context context, List<String> strList, boolean isVisible) {
        this.context = context;
        this.strList = strList;
        this.isVisible = isVisible;
    }

    public class ViewHolder {
        TextView tvName;
        ImageView ivDivide;
    }

    @Override
    public int getCount() {
        return strList.size();
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_alert_item, null);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tv_alert_list_item);
            holder.ivDivide = convertView.findViewById(R.id.iv_alert_divide);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 0 && !isVisible) {
            holder.ivDivide.setVisibility(View.GONE);
        } else {
            holder.ivDivide.setVisibility(View.VISIBLE);
        }
        holder.tvName.setText(strList.get(position));
        return convertView;
    }
}
