package com.qd.longchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.qd.longchat.R;
import com.longchat.base.model.QDCollectMessage;
import com.longchat.base.dao.QDMessage;
import com.qd.longchat.config.QDCollectContentLayout;
import com.qd.longchat.holder.QDCollectHolder;
import com.qd.longchat.util.QDDateUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/7/9 下午2:33
 */
public class QDCollectAdapter extends BaseAdapter {

    private Context context;
    private List<QDCollectMessage> messageList;
    private Map<String, View> viewMap;

    public QDCollectAdapter(Context context) {
        this.context = context;
        this.viewMap = new HashMap<>();
    }

    @Override
    public int getCount() {
        if (messageList == null)
            return 0;
        return messageList.size();
    }

    public void remove(int index) {
        messageList.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public QDCollectMessage getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDCollectHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_collect, null);
            holder = new QDCollectHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDCollectHolder) convertView.getTag();
        }
        QDCollectMessage message = messageList.get(position);
        View view = viewMap.get(message.getGid());
        if (view != null) {
            bindView(holder.itemContainer, view);
        } else {
            View contentView = getContentView(message);
            viewMap.put(message.getGid(), contentView);
            bindView(holder.itemContainer, contentView);
        }
        long time = message.getCreateDate();
        String date = QDDateUtil.getMsgTime(time * 1000);
        holder.itemInfo.setText(message.getSource() + "  " + date);
        return convertView;
    }

    private View getContentView(QDCollectMessage message) {
        String type = message.getMsgType();
        switch (type) {
            case QDMessage.MSG_TYPE_TEXT:
                return QDCollectContentLayout.createTextView(context, message);
            case QDMessage.MSG_TYPE_LOCATION:
                return QDCollectContentLayout.createLocationView(context, message);
            case QDMessage.MSG_TYPE_IMAGE:
                return QDCollectContentLayout.createImageView(context, message);
            case QDMessage.MSG_TYPE_FILE:
                return QDCollectContentLayout.createFileView(context, message);
            case QDMessage.MSG_TYPE_VOICE:
                return QDCollectContentLayout.createVoiceView(context, message);
            case QDMessage.MSG_TYPE_SHOOT:
                return QDCollectContentLayout.createVideoView(context, message);
            case QDMessage.MSG_TYPE_LINK:
                return QDCollectContentLayout.createLinkView(context, message);
        }
        return null;
    }

    private void bindView(FrameLayout container, View view) {
        container.removeAllViews();
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        if (viewGroup != null)
            viewGroup.removeAllViews();
        container.addView(view);
    }

    public void setMessageList(List<QDCollectMessage> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

}
