package com.qd.longchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.model.gd.QDGroupFileModel;
import com.qd.longchat.R;
import com.qd.longchat.holder.QDGroupFileHolder;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDUtil;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/8/2 下午3:18
 */
public class QDGroupFileAdapter extends BaseAdapter {

    private Context context;
    private List<QDGroupFileModel.ListBean> listBeanList;

    public QDGroupFileAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        if (listBeanList == null)
            return 0;
        return listBeanList.size();
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
        QDGroupFileHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group_file, null);
            holder = new QDGroupFileHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDGroupFileHolder) convertView.getTag();
        }

        QDGroupFileModel.ListBean bean = listBeanList.get(position);
        holder.ivItemIcon.setImageResource(QDUtil.getFileIconByName(context, bean.getName()));
        holder.tvItemName.setText(bean.getName());
        holder.tvItemSender.setText(bean.getCreateUname());
        holder.tvItemSize.setText(QDUtil.changFileSizeToString(bean.getSize()));
        holder.tvItemTime.setText(QDDateUtil.getConversationTime(bean.getCreateTime() * 1000));
        return convertView;
    }

    public void setListBeanList(List<QDGroupFileModel.ListBean> listBeanList) {
        this.listBeanList = listBeanList;
        notifyDataSetChanged();
    }
}
