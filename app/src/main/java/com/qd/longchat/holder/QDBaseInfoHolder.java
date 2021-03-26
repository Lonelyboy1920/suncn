package com.qd.longchat.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 2018/6/1 上午9:37
 */

public class QDBaseInfoHolder {

    @BindView(R2.id.iv_item_icon)
    public ImageView ivItemIcon;
    @BindView(R2.id.tv_item_name)
    public TextView tvItemName;
    @BindView(R2.id.tv_item_info)
    public TextView tvItemInfo;
    @BindView(R2.id.iv_item_arrow)
    public ImageView ivItemArrow;
    @BindView(R2.id.iv_item_line)
    public ImageView ivItemLine;

    public QDBaseInfoHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
