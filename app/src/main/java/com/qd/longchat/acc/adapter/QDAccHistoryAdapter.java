package com.qd.longchat.acc.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.longchat.base.model.gd.QDAccHistoryModel;
import com.longchat.base.model.gd.QDAccModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qd.longchat.R;
import com.qd.longchat.acc.holder.QDAccHistoryHolder;
import com.qd.longchat.activity.QDWebActivity;
import com.qd.longchat.application.QDApplication;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDIntentKeyUtil;
import com.qd.longchat.util.QDUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/26 下午6:25
 */
public class QDAccHistoryAdapter extends BaseAdapter {

    private Context context;
    private List<QDAccHistoryModel> modelList;
    private boolean isEnd;
    private QDAccModel accModel;

    public QDAccHistoryAdapter(Context context, QDAccModel accModel) {
        this.context = context;
        this.accModel = accModel;
    }

    @Override
    public int getCount() {
        if (modelList == null || modelList.size() == 0) {
            return 1;
        }
        return modelList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        QDAccHistoryHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_acc_history, null);
            holder = new QDAccHistoryHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDAccHistoryHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.llItemLayout.setVisibility(View.VISIBLE);
            holder.tvItemDesc.setVisibility(View.VISIBLE);
            holder.ivItemSpace.setVisibility(View.VISIBLE);
            holder.tvItemDesc.setText(accModel.getIntro());
            String url = accModel.getIcon();
            ImageLoader.getInstance().displayImage(QDUtil.replaceWebServerAndToken(url), holder.ivItemIcon, QDApplication.options);
            holder.tvItemName.setText(accModel.getName());
            holder.tvItemDesc.setText(accModel.getIntro());
        } else {
            holder.llItemLayout.setVisibility(View.GONE);
            holder.tvItemDesc.setVisibility(View.GONE);
            holder.ivItemSpace.setVisibility(View.GONE);
        }
        if (modelList != null && modelList.size() != 0) {
            QDAccHistoryModel model = modelList.get(position);
            holder.rlItemLayout.setVisibility(View.VISIBLE);
            holder.tvItemTitle.setText(model.getTitle());
            String articleUrl = model.getCover();
            ImageLoader.getInstance().displayImage(QDUtil.replaceWebServerAndToken(articleUrl), holder.ivItemPhoto, QDApplication.options);
            holder.tvItemTime.setText(QDDateUtil.longToString(model.getPublishTime() * 1000, QDDateUtil.ACC_FORMAT));
            if (isEnd && position == (getCount() - 1)) {
                holder.tvItemBottom.setVisibility(View.VISIBLE);
            } else {
                holder.tvItemBottom.setVisibility(View.GONE);
            }
        } else {
            holder.rlItemLayout.setVisibility(View.GONE);
            holder.tvItemBottom.setVisibility(View.VISIBLE);
        }

        holder.rlItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelList != null) {
                    QDAccHistoryModel model = modelList.get(position);
                    Intent intent = new Intent(context, QDWebActivity.class);
                    intent.putExtra(QDIntentKeyUtil.INTENT_ACC_ARTICLE, model);
                    intent.putExtra(QDIntentKeyUtil.INTENT_KEY_WEB_URL, QDUtil.replaceWebServerAndToken(model.getMobileUrl()));
                    context.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    public void setModelList(List<QDAccHistoryModel> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    public void addModeList(List<QDAccHistoryModel> modelList) {
        if (this.modelList == null) {
            this.modelList = new ArrayList<>();
        }
        this.modelList.addAll(modelList);
        notifyDataSetChanged();
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }
}
