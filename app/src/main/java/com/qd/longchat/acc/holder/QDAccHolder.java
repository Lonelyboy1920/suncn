package com.qd.longchat.acc.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qd.longchat.R;
import com.qd.longchat.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author skynight(skynight @ dingtalk.com)
 * @creatTime 2018/11/26 下午2:59
 */
public class QDAccHolder {

    @BindView(R2.id.tv_item_tag)
    public TextView tvItemTag;
    @BindView(R2.id.iv_item_icon)
    public ImageView ivItemIcon;
    @BindView(R2.id.tv_item_name)
    public TextView tvItem_name;
    @BindView(R2.id.iv_item_line)
    public ImageView ivItemLine;

    public QDAccHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
