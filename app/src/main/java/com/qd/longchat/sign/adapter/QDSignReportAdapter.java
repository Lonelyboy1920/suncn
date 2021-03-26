package com.qd.longchat.sign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.gson.JsonObject;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.util.QDGson;
import com.qd.longchat.R;
import com.qd.longchat.sign.holder.QDSignReportHolder;
import com.qd.longchat.util.QDDateUtil;

import java.util.List;


/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2017/12/29 下午3:16
 */

public class QDSignReportAdapter extends BaseAdapter {

    private Context context;
    private List<QDMessage> msgList;

    public QDSignReportAdapter(Context context, List<QDMessage> msgList) {
        this.context = context;
        this.msgList = msgList;
    }

    public void setMsgList(List<QDMessage> msgList) {
        this.msgList = msgList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return msgList.size();
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
        QDSignReportHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.im_item_report, null);
            holder = new QDSignReportHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDSignReportHolder) convertView.getTag();
        }
        QDMessage msg = msgList.get(position);
        JsonObject jsonObject = QDGson.getGson().fromJson(msg.getContent(), JsonObject.class);
        long time = jsonObject.get("create_time").getAsLong();
        String address = jsonObject.get("area").getAsString();
        holder.tvName.setText(msg.getSenderName());
        holder.tvTime.setText(QDDateUtil.longToString(time * 1000, QDDateUtil.DATE_FOR_MEETING));
        holder.tvMsgTime.setText(QDDateUtil.getMsgTime(msg.getCreateDate()/1000));
        holder.tvAddress.setText(address);
        return convertView;
    }

}
