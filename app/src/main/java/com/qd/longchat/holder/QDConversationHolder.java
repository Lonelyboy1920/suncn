package com.qd.longchat.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/5/19 下午12:41
 */

public class QDConversationHolder {

    @BindView(R2.id.iv_item_icon)
    public ImageView ivItemIcon;
    @BindView(R2.id.tv_item_name)
    public TextView tvItemName;
    @BindView(R2.id.tv_item_subname)
    public TextView tvItemSubname;
    @BindView(R2.id.tv_item_time)
    public TextView tvItemTime;
    @BindView(R2.id.tv_item_point)
    public TextView tvItemPoint;
    @BindView(R2.id.iv_item_top)
    public ImageView ivItemTop;
    @BindView(R2.id.ll_item_not_disturb)
    public LinearLayout llItemNotDisturbLayout;
    @BindView(R2.id.iv_item_disturb_point)
    public ImageView ivItemDisturbPoint;

    public QDConversationHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
