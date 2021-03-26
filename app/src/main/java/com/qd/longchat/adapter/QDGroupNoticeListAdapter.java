package com.qd.longchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.model.gd.QDGroupNoticeModel;
import com.qd.longchat.R;
import com.qd.longchat.holder.QDGroupNoticeListHolder;
import com.qd.longchat.util.QDDateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/15 上午10:15
 */
public class QDGroupNoticeListAdapter extends BaseAdapter {

    private Context context;
    private List<QDGroupNoticeModel> modelList;

    public QDGroupNoticeListAdapter(Context context) {
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
    public QDGroupNoticeModel getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDGroupNoticeListHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group_notice_list, null);
            holder = new QDGroupNoticeListHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDGroupNoticeListHolder) convertView.getTag();
        }

        QDGroupNoticeModel model = modelList.get(position);
        holder.tvItemDesc.setText(model.getNotice());
        holder.tvItemSender.setText(model.getCreateUname() + "  " + QDDateUtil.longToString(model.getCreateTime() * 1000, QDDateUtil.CLOUD_FORMAT));
        return convertView;
    }

    public void setModelList(List<QDGroupNoticeModel> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    public void addModel(QDGroupNoticeModel model) {
        if (modelList == null) {
            modelList = new ArrayList<>();
        }
        this.modelList.add(0, model);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        if (modelList != null) {
            modelList.remove(position);
            notifyDataSetChanged();
        }
    }
}
