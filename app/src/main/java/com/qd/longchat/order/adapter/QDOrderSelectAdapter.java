package com.qd.longchat.order.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qd.longchat.R;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/20 下午5:31
 */
public class QDOrderSelectAdapter extends BaseAdapter {

    private Context context;
    private List<String> selectList;

    public QDOrderSelectAdapter(Context context, List<String> selectList) {
        this.context = context;
        this.selectList = selectList;
    }

    public class Holder {
        TextView textView;
    }

    @Override
    public int getCount() {
        if (selectList == null) {
            return 0;
        }
        return selectList.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order_select, null);
            holder = new Holder();
            holder.textView = convertView.findViewById(R.id.tv_item_name);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.textView.setText(selectList.get(position));
        return convertView;
    }
}
