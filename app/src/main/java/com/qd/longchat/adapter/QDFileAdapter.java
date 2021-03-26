package com.qd.longchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.qd.longchat.R;
import com.qd.longchat.holder.QDFileHolder;
import com.qd.longchat.model.QDFile;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDUtil;

import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/5/4 下午4:51
 */

public class QDFileAdapter extends BaseAdapter {

    private Context context;
    private List<QDFile> fileList;

    public QDFileAdapter(Context context, List<QDFile> fileList) {
        this.context = context;
        this.fileList = fileList;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public QDFile getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setFileList(List<QDFile> fileList) {
        this.fileList = fileList;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDFileHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_file, null);
            holder = new QDFileHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDFileHolder) convertView.getTag();
        }
        QDFile file = fileList.get(position);
        if (file.isDirectory()) {
            holder.ivFileIcon.setBackgroundResource(R.mipmap.ic_file_folder);
            holder.tvFileSize.setText(context.getResources().getString(R.string.str_file) + " " + file.getTotal());
            holder.tvFileTime.setVisibility(View.GONE);
        } else {
            holder.ivFileIcon.setBackgroundResource(QDUtil.getFileIconByName(context, file.getName()));
            holder.tvFileSize.setText(file.getSize());
            holder.tvFileTime.setVisibility(View.VISIBLE);
            holder.tvFileTime.setText(QDDateUtil.getConversationTime(file.getDate()));
        }
        holder.tvFileName.setText(file.getName());
        return convertView;
    }
}
