package com.qd.longchat.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/8/2 下午3:16
 */
public class QDGroupFileHolder {

    @BindView(R2.id.iv_item_icon)
    public ImageView ivItemIcon;
    @BindView(R2.id.tv_item_name)
    public TextView tvItemName;
    @BindView(R2.id.tv_item_sender)
    public TextView tvItemSender;
    @BindView(R2.id.tv_item_size)
    public TextView tvItemSize;
    @BindView(R2.id.tv_item_time)
    public TextView tvItemTime;
    @BindView(R2.id.iv_item_line)
    public ImageView ivItemLine;

    public QDGroupFileHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
