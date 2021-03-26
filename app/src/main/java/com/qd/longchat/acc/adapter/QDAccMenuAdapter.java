package com.qd.longchat.acc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.model.gd.QDAccMenuModel;
import com.qd.longchat.R;
import com.qd.longchat.acc.holder.QDAccMenuHolder;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/27 下午3:02
 */
public class QDAccMenuAdapter extends BaseAdapter {

    private Context context;
    private List<QDAccMenuModel> modelList;

    public QDAccMenuAdapter(Context context, List<QDAccMenuModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public int getCount() {
        return modelList.size();
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
        QDAccMenuHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_acc_text, null);
            holder = new QDAccMenuHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDAccMenuHolder) convertView.getTag();
        }
        if (position == getCount() - 1) {
            holder.imageView.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
        }
        holder.textView.setText(modelList.get(position).getName());
        return convertView;
    }
}
