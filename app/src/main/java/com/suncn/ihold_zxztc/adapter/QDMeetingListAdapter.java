package com.suncn.ihold_zxztc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.model.gd.QDMeetingModel;
import com.qd.longchat.R;
import com.qd.longchat.meeting.holder.QDMeetingListHolder;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDUtil;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2019/1/15 下午2:00
 */
public class QDMeetingListAdapter extends BaseAdapter {

    private Context context;
    private List<QDMeetingModel> modelList;

    public QDMeetingListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        if (modelList == null) {
            return 0;
        }
        return modelList.size();
    }

    @Override
    public QDMeetingModel getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDMeetingListHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_meeting, null);
            holder = new QDMeetingListHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDMeetingListHolder) convertView.getTag();
        }

        QDMeetingModel model = modelList.get(position);
        holder.tvItemTitle.setText(QDUtil.decodeString(model.getTitle()));
        holder.tvItemSubname.setText("主持人: " + model.getCreateUname());
        holder.tvItemTime.setText(QDDateUtil.getMeetTime(model.getStartTime() * 1000));

        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime < model.getStartTime()) {
            holder.tvItemStatus.setText("未开始");
            holder.tvItemStatus.setTextColor(Color.parseColor("#666666"));
        } else if (currentTime > model.getEndTime()) {
            holder.tvItemStatus.setText("已结束");
            holder.tvItemStatus.setTextColor(Color.parseColor("#666666"));
        } else {
            holder.tvItemStatus.setText("进行中");
            holder.tvItemStatus.setTextColor(context.getResources().getColor(R.color.view_head_bg));
        }

        return convertView;
    }


    public void setModelList(List<QDMeetingModel> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }
}
