package com.qd.longchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.QDClient;
import com.longchat.base.dao.QDMessage;
import com.longchat.base.notify.QDNFAppInfo;
import com.longchat.base.util.QDGson;
import com.qd.longchat.R;
import com.qd.longchat.holder.QDNoticeHolder;
import com.qd.longchat.util.QDDateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2019/3/9 上午11:30
 */
public class QDNoticeAdapter extends BaseAdapter {

    private Context context;
    private List<QDMessage> dataList;
    private List<String> idList;

    public QDNoticeAdapter(Context context) {
        this.context = context;
        this.dataList = new ArrayList<>();
        this.idList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return dataList.size();
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
        QDNoticeHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_notice, null);
            holder = new QDNoticeHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDNoticeHolder) convertView.getTag();
        }
        QDMessage data = dataList.get(position);
        long offsetTime = QDClient.getInstance().getLoginInfo().getSTimeOffset(); //服务器和本地的时间差
        long msgTime = (data.getCreateDate() / 1000) - offsetTime; //微秒变为毫秒
        holder.tvTime.setVisibility(View.VISIBLE);
        holder.tvTime.setText(QDDateUtil.getMsgTime(msgTime));
        if (data.getAvatar().equalsIgnoreCase("icon_notice_dayamount")) {
            holder.ivIcon.setImageResource(R.drawable.ic_early_warning);
            holder.tvSenderTag.setVisibility(View.GONE);
            holder.tvSender.setVisibility(View.GONE);
            holder.tvTitle.setText(R.string.str_early_warning);
        } else if (data.getAvatar().equalsIgnoreCase("icon_notice_seword")) {
            holder.ivIcon.setImageResource(R.drawable.ic_bad_word);
            holder.tvSenderTag.setVisibility(View.VISIBLE);
            holder.tvSender.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(R.string.str_bad_word_warning);
        }
        QDNFAppInfo info = QDGson.getGson().fromJson(data.getContent(), QDNFAppInfo.class);
        holder.tvSender.setText(info.getSendName());
        holder.tvSenderTime.setText(QDDateUtil.longToString(info.getSendTime() * 1000, QDDateUtil.MSG_FORMAT4));
        holder.tvContent.setText(info.getDesc());
        return convertView;
    }

    public void setDataList(List<QDMessage> dataList) {
        this.dataList.clear();
        idList.clear();
        for (QDMessage data : dataList) {
            if (!idList.contains(data.getMsgId())) {
                idList.add(data.getMsgId());
                this.dataList.add(data);
            }
        }
        notifyDataSetChanged();
    }

    public void addDataList(List<QDMessage> dataList) {
        for (QDMessage data : dataList) {
            if (!idList.contains(data.getMsgId())) {
                idList.add(data.getMsgId());
                this.dataList.add(data);
            }
        }
        notifyDataSetChanged();
    }
}
