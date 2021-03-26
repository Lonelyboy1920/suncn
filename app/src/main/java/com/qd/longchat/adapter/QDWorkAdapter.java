package com.qd.longchat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.longchat.base.dao.QDApp;
import com.qd.longchat.R;
import com.qd.longchat.holder.QDWorkHolder;
import com.qd.longchat.util.QDUtil;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.List;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/4/13 下午6:03
 */

public class QDWorkAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {

    private Context context;
    private List<QDApp> appList;

    public QDWorkAdapter(Context context, List<QDApp> appList) {
        this.context = context;
        this.appList = appList;
    }

    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public QDApp getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDWorkHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.im_item_work, parent, false);
            holder = new QDWorkHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDWorkHolder) convertView.getTag();
        }
        QDApp app = appList.get(position);
        String icon = QDUtil.getWebFileServer() + app.getIcon();
        Glide.with(context).load(icon).apply(new RequestOptions().skipMemoryCache(true).placeholder(R.mipmap.ic_download_loading)
                .error(R.mipmap.ic_download_failed)).into(holder.itemIcon);
        holder.itemName.setText(app.getName());
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return appList.get(position).getGroup();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_title, null);
            holder.textView = convertView.findViewById(R.id.tv_item_title);
            holder.imageView = convertView.findViewById(R.id.iv_item_line);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        String groupName = appList.get(position).getGroupName();
        if (position != 0) {
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(groupName)) {
            holder.textView.setText(R.string.all_app);
        } else {
            holder.textView.setText(groupName);
        }
        return convertView;
    }

    public class Holder {
        public TextView textView;
        public ImageView imageView;
    }
}
