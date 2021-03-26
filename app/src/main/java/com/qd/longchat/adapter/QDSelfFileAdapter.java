package com.qd.longchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.qd.longchat.R;
import com.qd.longchat.holder.QDSelfFileHolder;
import com.qd.longchat.model.QDFile;
import com.qd.longchat.util.QDDateUtil;
import com.qd.longchat.util.QDUtil;

import java.util.Date;
import java.util.List;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/8/16 下午2:20
 */
public class QDSelfFileAdapter extends BaseAdapter {

    private Context context;
    private List<QDFile> fileList;

    public QDSelfFileAdapter(Context context, List<QDFile> fileList) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QDSelfFileHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_self_file, null);
            holder = new QDSelfFileHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (QDSelfFileHolder) convertView.getTag();
        }
        QDFile file = fileList.get(position);
        String name = file.getName();
        holder.itemIcon.setImageResource(QDUtil.getFileIconByName(context, name));
        holder.itemName.setText(name);
        holder.itemSize.setText(file.getSize());
        holder.itemTag.setText(file.getInfo());

        String date = QDDateUtil.dateToString(new Date(file.getDate()), QDDateUtil.FILE_FORMAT);
        if (position == 0) {
            holder.itemTimeLayout.setVisibility(View.VISIBLE);
            holder.itemDate.setText(date);
        } else {
            QDFile lastFile = fileList.get(position - 1);
            String lastDate = QDDateUtil.dateToString(new Date(lastFile.getDate()), QDDateUtil.FILE_FORMAT);
            if (!lastDate.equalsIgnoreCase(date)) {
                holder.itemTimeLayout.setVisibility(View.VISIBLE);
                holder.itemDate.setText(date);
            } else {
                holder.itemTimeLayout.setVisibility(View.GONE);
            }
        }
        holder.itemTime.setText(QDDateUtil.dateToString(new Date(file.getDate()), QDDateUtil.MSG_FORMAT1));
        return convertView;
    }
}
