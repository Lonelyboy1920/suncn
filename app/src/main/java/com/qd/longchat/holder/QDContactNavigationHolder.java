package com.qd.longchat.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/30 下午4:04
 */

public class QDContactNavigationHolder extends RecyclerView.ViewHolder {

    @BindView(R2.id.tv_item_navigation_name)
    public TextView tvNavigationName;
    @BindView(R2.id.iv_item_navigation_icon)
    public ImageView ivNavigationIcon;

    public QDContactNavigationHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}
