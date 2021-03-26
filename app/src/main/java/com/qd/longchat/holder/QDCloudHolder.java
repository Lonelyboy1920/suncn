package com.qd.longchat.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/10/16 下午3:34
 */
public class QDCloudHolder {

    @BindView(R2.id.iv_item_icon)
    public ImageView ivItemIcon;
    @BindView(R2.id.tv_item_name)
    public TextView tvItemName;
    @BindView(R2.id.tv_item_info)
    public TextView tvItemInfo;
    @BindView(R2.id.pb_item_progress)
    public ProgressBar pbItemProgress;
    @BindView(R2.id.iv_item_more)
    public ImageView ivItemMore;
    @BindView(R2.id.tv_item_size)
    public TextView tvItemSize;

    public QDCloudHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
