package com.qd.longchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.qd.longchat.R;
import com.longchat.base.util.QDLog;
import com.longchat.base.util.QDUtil;
import com.qd.longchat.holder.QDPhotoSelectHolder;
import com.qd.longchat.model.QDPhoto;


import java.io.File;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/4 上午10:19
 */

public class QDPhotoSelectAdapter extends BaseAdapter {
    private Context context;
    private List<QDPhoto> photoList;
    private OnPhotoSelectedListener listener;
    private List<String> selectedPhotoList;
    private boolean isSingle;

    public QDPhotoSelectAdapter(Context context, List<QDPhoto> photoList, List<String> selectedPhotoList, boolean isSingle) {
        this.context = context;
        this.photoList = photoList;
        this.selectedPhotoList = selectedPhotoList;
        this.isSingle = isSingle;
    }

    @Override
    public int getCount() {
        return photoList.size();
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
        final QDPhotoSelectHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_photo_select, null);
            holder = new QDPhotoSelectHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDPhotoSelectHolder) convertView.getTag();
        }
        QDPhoto photo = photoList.get(position);
        final boolean[] isSelected = new boolean[1];
        final String path = photo.getPath();
        if (selectedPhotoList.contains(path)) {
            isSelected[0] = true;
        } else {
            isSelected[0] = false;
        }
        File file = new File(photo.getPath());
        Glide.with(context).load(file).into(holder.photo);
        if (isSingle) {
            holder.photoSelect.setVisibility(View.GONE);
        } else {
            holder.photoSelect.setVisibility(View.VISIBLE);

            if (isSelected[0]) {
                holder.photoSelect.setImageResource(R.drawable.ic_round_selected);
            } else {
                holder.photoSelect.setImageResource(R.drawable.ic_round_unselected);
            }

            holder.photoSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelected[0]) {
                        holder.photoSelect.setImageResource(R.drawable.ic_round_unselected);
                        listener.onPhotoSelected(path, false);
                        isSelected[0] = false;
                    } else {
                        if (selectedPhotoList.size() == 9) {
                            QDUtil.showToast(context, context.getResources().getString(R.string.photo_select_had_nine_pic));
                            return;
                        }
                        holder.photoSelect.setImageResource(R.drawable.ic_round_selected);
                        listener.onPhotoSelected(path, true);
                        isSelected[0] = true;
                    }
                    QDLog.e("111", "======= path size: " + selectedPhotoList.size() + "=====");
                }
            });
        }
        return convertView;
    }

    public void setListener(OnPhotoSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnPhotoSelectedListener {
        void onPhotoSelected(String path, boolean isAdd);
    }

    public void setPhotoList(List<QDPhoto> photoList) {
        this.photoList = photoList;
        notifyDataSetChanged();
    }
}
