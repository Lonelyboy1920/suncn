package com.qd.longchat.order.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.model.gd.QDOrderFeedbackModel;
import com.qd.longchat.R;
import com.qd.longchat.order.holder.QDFeedbackHolder;
import com.qd.longchat.util.QDBitmapUtil;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDUtil;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/9/27 下午4:16
 */
public class QDOrderFeedbackAdapter extends BaseAdapter {

    private Context context;
    private List<QDOrderFeedbackModel.ListBean> beanList;

    public QDOrderFeedbackAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        if (beanList == null) {
            return 0;
        }
        return beanList.size();
    }

    @Override
    public QDOrderFeedbackModel.ListBean getItem(int position) {
        return beanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDFeedbackHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order_feedback, null);
            holder = new QDFeedbackHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDFeedbackHolder) convertView.getTag();
        }
        QDOrderFeedbackModel.ListBean bean = beanList.get(position);
        Bitmap bitmap = QDBitmapUtil.getInstance().createNameAvatar((Activity) context, bean.getCreateUid(), bean.getCreateUname());

        holder.ivItemIcon.setImageBitmap(bitmap);
        holder.tvItemName.setText(bean.getCreateUname());
        int state = bean.getStatus();
        if (state == 1) {
            holder.tvItemState.setBackgroundResource(R.drawable.ic_order_done);
            holder.tvItemState.setTextColor(Color.parseColor("#A2D8C6"));
            holder.tvItemState.setText("已完成");
        } else {
            holder.tvItemState.setBackgroundResource(R.drawable.ic_order_undone);
            holder.tvItemState.setTextColor(Color.parseColor("#FDA6A6"));
            holder.tvItemState.setText("未完成");
        }
        holder.tvItemTime.setText(QDDateUtil.longToString(bean.getCreateTime() * 1000, QDDateUtil.DATE_FOR_ORDER));
        holder.tvItemContent.setText(QDUtil.decodeString(bean.getContent()));
        return convertView;
    }

    public void setBeanList(List<QDOrderFeedbackModel.ListBean> beanList) {
        this.beanList = beanList;
    }
}
