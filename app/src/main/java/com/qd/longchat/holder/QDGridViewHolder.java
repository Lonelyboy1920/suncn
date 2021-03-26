package com.qd.longchat.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;


/**
 * Created by xxw on 2016/7/20.
 */
public class QDGridViewHolder {
    public ImageView gridImage;
    public TextView gridInfo;

    public static QDGridViewHolder initView(View view) {
        QDGridViewHolder holder = new QDGridViewHolder();
        holder.gridImage = view.findViewById(R.id.iv_grid_image);
        holder.gridInfo = view.findViewById(R.id.tv_grid_info);
        return holder;
    }
}
