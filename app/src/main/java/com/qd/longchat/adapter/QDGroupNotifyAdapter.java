package com.qd.longchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.dao.QDGroupNotice;
import com.qd.longchat.R;
import com.qd.longchat.holder.QDConversationHolder;
import com.qd.longchat.util.QDDateUtil;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/11 下午4:13
 */
public class QDGroupNotifyAdapter extends BaseAdapter {

    private Context context;
    private List<QDGroupNotice> noticeList;

    public QDGroupNotifyAdapter(Context context, List<QDGroupNotice> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
    }

    @Override
    public int getCount() {
        return noticeList.size();
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
        QDConversationHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_conversation, null);
            holder = new QDConversationHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDConversationHolder) convertView.getTag();
        }
        QDGroupNotice notice = noticeList.get(position);
        holder.ivItemIcon.setImageResource(R.mipmap.im_recent_group_icon);
        holder.tvItemName.setText(notice.getName());
        holder.tvItemSubname.setText(notice.getSubject());
        holder.tvItemTime.setText(QDDateUtil.getConversationTime(notice.getTime()));
        holder.ivItemTop.setVisibility(View.GONE);
        return convertView;
    }
}
