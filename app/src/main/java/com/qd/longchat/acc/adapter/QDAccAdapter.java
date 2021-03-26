package com.qd.longchat.acc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.model.gd.QDAccModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qd.longchat.R;
import com.qd.longchat.acc.holder.QDAccHolder;
import com.qd.longchat.application.QDApplication;
import com.qd.longchat.util.QDUtil;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/26 下午2:58
 */
public class QDAccAdapter extends BaseAdapter {

    private Context context;
    private List<QDAccModel> modelList;
    private boolean isHaveTag;

    public QDAccAdapter(Context context, boolean isHaveTag) {
        this.context = context;
        this.isHaveTag = isHaveTag;
    }

    @Override
    public int getCount() {
        if (modelList == null) {
            return 0;
        }
        return modelList.size();
    }

    @Override
    public QDAccModel getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDAccHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_acc, null);
            holder = new QDAccHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDAccHolder) convertView.getTag();
        }
        QDAccModel model = modelList.get(position);
        if (isHaveTag) {
            int isFollow = model.getIsFollow(); //0 未关注 1 已关注
            if (position == 0) {
                holder.tvItemTag.setVisibility(View.VISIBLE);
                if (isFollow == 0) {
                    holder.tvItemTag.setText(context.getResources().getString(R.string.acc_not_follow));
                } else {
                    holder.tvItemTag.setText(context.getResources().getString(R.string.acc_followed));
                }
            } else {
                int isFollow1 = modelList.get(position - 1).getIsFollow();
                if (isFollow != isFollow1) {
                    holder.tvItemTag.setVisibility(View.VISIBLE);
                    holder.tvItemTag.setText(context.getResources().getString(R.string.acc_not_follow));
                } else {
                    holder.tvItemTag.setVisibility(View.GONE);
                }
            }
        } else {
            holder.tvItemTag.setVisibility(View.GONE);
        }
        String iconUrl = model.getIcon();
        ImageLoader.getInstance().displayImage(QDUtil.replaceWebServerAndToken(iconUrl), holder.ivItemIcon, QDApplication.options);
        holder.tvItem_name.setText(model.getName());
        return convertView;
    }

    public void setModelList(List<QDAccModel> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }
}
