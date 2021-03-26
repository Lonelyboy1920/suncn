package com.qd.longchat.acc.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/26 下午6:25
 */
public class QDAccHistoryHolder {

    @BindView(R2.id.ll_item_layout)
    public LinearLayout llItemLayout;
    @BindView(R2.id.iv_item_icon)
    public ImageView ivItemIcon;
    @BindView(R2.id.tv_item_desc)
    public TextView tvItemDesc;
    @BindView(R2.id.tv_item_name)
    public TextView tvItemName;
    @BindView(R2.id.iv_item_space)
    public ImageView ivItemSpace;
    @BindView(R2.id.iv_item_photo)
    public ImageView ivItemPhoto;
    @BindView(R2.id.tv_item_title)
    public TextView tvItemTitle;
    @BindView(R2.id.tv_item_time)
    public TextView tvItemTime;
    @BindView(R2.id.tv_item_bottom)
    public TextView tvItemBottom;
    @BindView(R2.id.rl_item_content_layout)
    public RelativeLayout rlItemLayout;

    public QDAccHistoryHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
