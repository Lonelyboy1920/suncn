package com.qd.longchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.qd.longchat.R;
import com.qd.longchat.holder.QDBucketHolder;
import com.qd.longchat.model.QDAlbum;
import com.qd.longchat.model.QDPhoto;


import java.io.File;
import java.util.List;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/4 下午2:54
 */

public class QDBucketAdapter extends BaseAdapter {

    private Context context;
    private List<QDAlbum> albumList;

    public QDBucketAdapter(Context context, List<QDAlbum> albumList) {
        this.context = context;
        this.albumList = albumList;
    }

    @Override
    public int getCount() {
        return albumList.size();
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
        QDBucketHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bucket, null);
            holder = new QDBucketHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDBucketHolder) convertView.getTag();
        }
        QDAlbum album = albumList.get(position);
        List<QDPhoto> photoList = album.getPhotoList();
        File file = new File(photoList.get(0).getPath());
        Glide.with(context).load(file).into(holder.icon);
        holder.name.setText(album.getName() + "(" + photoList.size() + ")");
        return convertView;
    }
}
