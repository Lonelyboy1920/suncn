package com.qd.longchat.order.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.model.QDOrder;
import com.qd.longchat.R;
import com.qd.longchat.order.holder.QDOrderHolder;
import com.qd.longchat.util.QDDateUtil;

import java.util.List;


/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/20 下午2:33
 */
public class QDOrderAdapter extends BaseAdapter {

    private Context context;
    private List<QDOrder> orderList;

    public QDOrderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        if (orderList == null) {
            return 0;
        }
        return orderList.size();
    }

    @Override
    public QDOrder getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDOrderHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order, null);
            holder = new QDOrderHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDOrderHolder) convertView.getTag();
        }
        QDOrder order = orderList.get(position);
        holder.itemTitle.setText(order.getTitle());
        holder.itemContent.setText(order.getContent());
        long createTime = order.getCreateTime() * 1000;
        String time = QDDateUtil.longToString(createTime, QDDateUtil.DATE_FOR_MEETING);
        holder.itemInfo.setText(order.getUserName() + " " + time);
        int status = order.getIsRead();
        if (status == QDOrder.STATUS_UNREAD) {
            holder.itemUnread.setVisibility(View.VISIBLE);
        } else {
            holder.itemUnread.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void setOrderList(List<QDOrder> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }
}
